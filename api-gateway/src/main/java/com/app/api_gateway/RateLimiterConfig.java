package com.app.api_gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    /**
     * Rate-limit per authenticated user (X-Auth-User header set by AuthFilter).
     * Falls back to the remote IP if the header is absent (e.g. public endpoints).
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String user = exchange.getRequest().getHeaders().getFirst("X-Auth-User");
            if (user != null && !user.isBlank()) {
                return Mono.just(user);
            }
            // fallback: use remote IP
            return Mono.just(
                exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "anonymous"
            );
        };
    }

    /**
     * Default limiter: 20 req/s with a burst of 40.
     * Override per-route in application.yml if needed.
     */
    @Bean
    public RedisRateLimiter defaultRateLimiter() {
        return new RedisRateLimiter(20, 40, 1);
    }
}