package com.eventhub.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ApiKeyForwardingFilter implements GlobalFilter, Ordered {

    @Value("${api.keys.auth}")
    private String authKey;

    @Value("${api.keys.event}")
    private String eventKey;

    @Value("${api.keys.booking}")
    private String bookingKey;

    @Value("${api.keys.payment}")
    private String paymentKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String apiKey = selectApiKeyForPath(path);

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-API-KEY", apiKey)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private String selectApiKeyForPath(String path) {
        if (path.startsWith("/auth") || path.startsWith("/users")) {
            return authKey;
        } else if (path.startsWith("/events")) {
            return eventKey;
        } else if (path.startsWith("/bookings")) {
            return bookingKey;
        } else if (path.startsWith("/payments") || path.startsWith("/notifications")) {
            return paymentKey;
        }
        return authKey;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
