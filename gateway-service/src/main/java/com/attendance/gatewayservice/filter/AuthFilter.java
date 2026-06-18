package com.attendance.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局鉴权过滤器
 * 统一验证请求中的 token 参数，并设置响应编码
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.info("Gateway 收到请求: {}", path);

        // 获取请求参数中的 token
        String token = request.getQueryParams().getFirst("token");

        // 验证 token（演示用，实际项目中应使用 JWT 等方式）
        if (token == null || !token.equals("1")) {
            log.warn("鉴权失败：token 无效或缺失，请求路径: {}", path);
            // 响应 http 状态码（401）
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            // 请求结束
            return exchange.getResponse().setComplete();
        }

        log.info("鉴权通过，放行请求: {}", path);
        // 继续执行过滤器链，并在响应时设置编码头
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            // 强制设置响应编码为 UTF-8
            response.getHeaders().set("Content-Type", "application/json; charset=UTF-8");
        }));
    }

    @Override
    public int getOrder() {
        // 过滤器顺序，数字越小优先级越高
        return 0;
    }
}
