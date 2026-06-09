package com.company.auth.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RoleAuthFilter implements GlobalFilter, Ordered {

    private static final Map<String, List<String>> ROLE_REQUIREMENTS = Map.of(
            "/api/roles/", List.of("admin", "super_admin"),
            "/api/audit/", List.of("admin", "super_admin")
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String rolesHeader = exchange.getRequest().getHeaders().getFirst("X-User-Roles");

        // Find matching role requirement
        for (var entry : ROLE_REQUIREMENTS.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                if (rolesHeader == null || rolesHeader.isEmpty()) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                List<String> userRoles = Arrays.asList(rolesHeader.split(","));
                boolean hasRole = userRoles.stream().anyMatch(role ->
                        entry.getValue().stream().anyMatch(role::equalsIgnoreCase));

                if (!hasRole) {
                    log.warn("Access denied: path={}, userRoles={}, required={}", path, userRoles, entry.getValue());
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
                break;
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0; // Runs after JwtAuthFilter (-1) which sets X-User-Roles header
    }
}
