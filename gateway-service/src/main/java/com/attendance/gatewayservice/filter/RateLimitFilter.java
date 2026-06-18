package com.attendance.gatewayservice.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单的滑动窗口限流过滤器
 * 阈值通过配置项控制，默认 60 秒内最多允许 60 个请求
 */
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    // 按“请求标识 + 请求路径”分桶，避免录制多个接口时互相影响
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();

    private final int maxRequestsPerWindow;
    private final long windowSizeMs;

    // 窗口开始时间
    private volatile long windowStartTime = System.currentTimeMillis();

    public RateLimitFilter(
            @Value("${ea.gateway.rate-limit.max-requests:60}") int maxRequestsPerWindow,
            @Value("${ea.gateway.rate-limit.window-seconds:60}") long windowSeconds) {
        this.maxRequestsPerWindow = Math.max(1, maxRequestsPerWindow);
        this.windowSizeMs = Math.max(1L, windowSeconds) * 1000L;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getQueryParams().getFirst("token");
        String clientKey = token != null ? token : "anonymous";
        String path = exchange.getRequest().getURI().getPath();
        String key = clientKey + ":" + path;

        // 检查是否需要重置窗口
        long currentTime = System.currentTimeMillis();
        if (currentTime - windowStartTime > windowSizeMs) {
            synchronized (this) {
                if (currentTime - windowStartTime > windowSizeMs) {
                    windowStartTime = currentTime;
                    requestCounts.clear();
                }
            }
        }

        // 获取或创建计数器
        AtomicInteger counter = requestCounts.computeIfAbsent(key, k -> new AtomicInteger(0));

        // 增加计数
        int currentCount = counter.incrementAndGet();

        int remaining = Math.max(0, maxRequestsPerWindow - currentCount);
        long retryAfterSeconds = Math.max(1L, (windowSizeMs - (currentTime - windowStartTime) + 999L) / 1000L);

        exchange.getResponse().getHeaders().set("X-RateLimit-Limit", String.valueOf(maxRequestsPerWindow));
        exchange.getResponse().getHeaders().set("X-RateLimit-Remaining", String.valueOf(remaining));

        if (currentCount > maxRequestsPerWindow) {
            // 超过限制，返回 429 Too Many Requests
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().set("Content-Type", "application/json;charset=UTF-8");
            exchange.getResponse().getHeaders().set("Retry-After", String.valueOf(retryAfterSeconds));
            String body = "{\"code\":429,\"message\":\"请求过于频繁，请稍后再试！\",\"data\":null}";
            return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8)))
            );
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 在鉴权过滤器之前执行
        return -100;
    }
}
