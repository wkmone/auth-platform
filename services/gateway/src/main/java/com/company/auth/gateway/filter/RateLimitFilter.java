package com.company.auth.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    private static final String SLIDING_WINDOW_LUA = """
            local key = KEYS[1]
            local limit = tonumber(ARGV[1])
            local window = tonumber(ARGV[2])
            local now = redis.call('TIME')
            local current = math.floor(now[1] * 1000 + now[2] / 1000)
            local windowStart = current - window * 1000
            redis.call('ZREMRANGEBYSCORE', key, 0, windowStart)
            local count = redis.call('ZCARD', key)
            if count >= limit then
                return 0
            end
            redis.call('ZADD', key, current, current .. '-' .. math.random())
            redis.call('EXPIRE', key, math.ceil(window * 2))
            return 1
            """;

    public RateLimitFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String ip = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";

        // IP-based rate limit: 60 req/min (30 for /oauth2/token)
        int ipLimit = path.startsWith("/oauth2/token") ? 30 : 60;
        String ipKey = "ratelimit:ip:" + ip;

        RedisScript<Long> script = RedisScript.of(SLIDING_WINDOW_LUA, Long.class);

        return redisTemplate.<Long>execute(script, List.of(ipKey), List.of(String.valueOf(ipLimit), "60"))
                .next()
                .onErrorResume(e -> {
                    log.warn("Redis rate limit check failed, allowing request: {}", e.getMessage());
                    return Mono.just(1L);
                })
                .flatMap(allowed -> {
                    if (allowed == 0L) {
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        exchange.getResponse().getHeaders().set("Retry-After", "60");
                        return exchange.getResponse().setComplete();
                    }

                    // User-based rate limit: 200 req/min (if user is authenticated)
                    String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
                    if (userId != null && !userId.isEmpty()) {
                        String userKey = "ratelimit:user:" + userId;
                        return redisTemplate.<Long>execute(script, List.of(userKey), List.of("200", "60"))
                                .next()
                                .onErrorResume(e -> {
                                    log.warn("Redis user rate limit check failed, allowing request: {}", e.getMessage());
                                    return Mono.just(1L);
                                })
                                .flatMap(userAllowed -> {
                                    if (userAllowed == 0L) {
                                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                                        exchange.getResponse().getHeaders().set("Retry-After", "60");
                                        return exchange.getResponse().setComplete();
                                    }
                                    return chain.filter(exchange);
                                });
                    }

                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
