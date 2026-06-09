package com.company.auth.gateway.filter;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final List<String> SKIP_PATHS = List.of(
            "/oauth2/", "/.well-known/", "/login", "/oauth2/consent", "/actuator/"
    );

    private final WebClient webClient;

    private volatile JWKSet jwkSet;
    private volatile long lastFetchTime = 0;
    private static final long CACHE_TTL_MS = 300_000; // 5 minutes

    public JwtAuthFilter(@Value("${auth.service.url:http://localhost:9001}") String authServiceUrl) {
        this.webClient = WebClient.create(authServiceUrl);
    }

    @PostConstruct
    public void init() {
        fetchJwkSet();
    }

    private void fetchJwkSet() {
        try {
            String jwksJson = webClient.get()
                    .uri("/oauth2/jwks")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(5));
            if (jwksJson != null) {
                this.jwkSet = JWKSet.parse(jwksJson);
                this.lastFetchTime = System.currentTimeMillis();
            }
        } catch (Exception e) {
            log.error("Failed to fetch JWK Set from auth service", e);
        }
    }

    private void refreshIfNeeded() {
        if (jwkSet == null || System.currentTimeMillis() - lastFetchTime > CACHE_TTL_MS) {
            synchronized (this) {
                if (jwkSet == null || System.currentTimeMillis() - lastFetchTime > CACHE_TTL_MS) {
                    fetchJwkSet();
                }
            }
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip public paths
        if (SKIP_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            refreshIfNeeded();

            if (jwkSet == null) {
                log.warn("JWK Set not available");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            SignedJWT signedJWT = SignedJWT.parse(token);
            String keyId = signedJWT.getHeader().getKeyID();

            JWK jwk = jwkSet.getKeyByKeyId(keyId);
            if (jwk == null) {
                log.warn("No JWK found for key ID: {}, attempting refresh", keyId);
                fetchJwkSet();
                if (jwkSet != null) {
                    jwk = jwkSet.getKeyByKeyId(keyId);
                }
                if (jwk == null) {
                    log.warn("No JWK found for key ID: {} after refresh", keyId);
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }

            if (!(jwk instanceof RSAKey rsaKey)) {
                log.warn("JWK is not an RSA key");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            RSAPublicKey publicKey = rsaKey.toRSAPublicKey();
            JWSVerifier verifier = new RSASSAVerifier(publicKey);

            if (!signedJWT.verify(verifier)) {
                log.warn("JWT signature verification failed");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Check expiration
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration != null && expiration.before(new Date())) {
                log.warn("JWT is expired");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Extract claims
            var claimsSet = signedJWT.getJWTClaimsSet();
            String subject = claimsSet.getSubject();
            String username = claimsSet.getStringClaim("username");
            Object rolesObj = claimsSet.getClaim("roles");
            String rolesStr = "";
            if (rolesObj instanceof List<?> rolesList) {
                rolesStr = rolesList.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(","));
            }

            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", subject != null ? subject : "")
                    .header("X-Username", username != null ? username : "")
                    .header("X-User-Roles", rolesStr)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (ParseException e) {
            return unauthorized(exchange, "JWT parsing failed: " + e.getMessage());
        } catch (JOSEException e) {
            return unauthorized(exchange, "JWT verification failed: " + e.getMessage());
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String logMessage) {
        log.warn("JWT auth failed: {}", logMessage);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().set(HttpHeaders.WWW_AUTHENTICATE,
                "Bearer error=\"invalid_token\"");
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
