package com.attendance.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局鉴权过滤器
 * 统一验证请求中的 token 参数
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
        // 继续执行过滤器链中的下一个资源
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 过滤器顺序，数字越小优先级越高
        return 0;
    }
}
