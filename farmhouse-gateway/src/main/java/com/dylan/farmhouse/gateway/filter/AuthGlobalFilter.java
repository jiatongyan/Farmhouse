package com.dylan.farmhouse.gateway.filter;

import com.dylan.farmhouse.common.result.Result;
import com.dylan.farmhouse.common.result.ResultCode;
import com.dylan.farmhouse.common.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT 鉴权全局过滤器：校验 Token 并把用户信息透传给下游服务。
 */
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper;

    /** 白名单：无需鉴权即可访问 */
    private static final List<String> WHITE_LIST = List.of(
            "/api/user/login",
            "/api/user/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (WHITE_LIST.contains(path)) {
            return chain.filter(exchange);
        }

        String auth = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return unauthorized(exchange, "未登录或登录已过期");
        }

        Claims claims;
        try {
            claims = JwtUtil.parseToken(auth.substring(7));
        } catch (ExpiredJwtException e) {
            return unauthorized(exchange, "登录已过期，请重新登录");
        } catch (JwtException | IllegalArgumentException e) {
            return unauthorized(exchange, "无效的令牌");
        }

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", String.valueOf(JwtUtil.getUserId(claims)))
                .header("X-User-Role", String.valueOf(JwtUtil.getRole(claims)))
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(
                    Result.fail(ResultCode.UNAUTHORIZED.getCode(), message));
        } catch (JsonProcessingException e) {
            bytes = "{\"code\":401,\"message\":\"未授权\"}".getBytes(StandardCharsets.UTF_8);
        }
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
