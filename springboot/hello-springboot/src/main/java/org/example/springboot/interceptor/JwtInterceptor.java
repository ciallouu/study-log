package org.example.springboot.interceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springboot.annotation.RequireRole;
import org.example.springboot.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * JWT 拦截器（第一块：只做"有没有票"，验签留到下一步）。
 *
 * <p>TODO(源)：读完后在下面三个位置的注释里各写一句"为什么这样写"，我只看注释和验收结果。
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // ① 头名照 HTTP 规范写，大小写不敏感（Tomcat 内部会把头名规范化）,
        String header = request.getHeader("Authorization");

        // TODO(源) 这里写一句：为什么这两种情况都要拒绝？排掉为空和没有Bearer 为开头的头格式
        if (header == null || !header.startsWith("Bearer ")) {
            writeUnauthorized(response, "未登录或凭证缺失");
            // TODO(源) 写一句：return false 之后，Controller 还会被调用吗？不会
            return false;
        }

        // ③ 验签：签名不对 / 过期 / 格式错，一律 401
        String token = header.substring(7);
        try {
            // Claims 的取法与 createToken 的对应、IllegalArgumentException 为什么实际打不到，
            // 见 docs/问答记录.md「2026-11-27 JWT 三个概念」。
            // request 的生命周期 = 一次请求，所以身份放这里，多个请求之间天然隔离。
            Claims claims = jwtUtil.parse(token);
            request.setAttribute("ctx.userId",   Integer.valueOf(claims.getSubject()));
            request.setAttribute("ctx.username", claims.get("username", String.class));
            request.setAttribute("ctx.role",     claims.get("role", Integer.class));
        } catch (JwtException | IllegalArgumentException e) {
            writeUnauthorized(response, "凭证无效或已过期");
            return false;
        }

        // ④ 授权：认证回答"你是谁"，这一步回答"你能做什么"。
        // handler 就是这次请求要执行的那个方法（HandlerMethod），能读到它身上的注解。
        if (handler instanceof HandlerMethod handlerMethod) {
            RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
            if (requireRole == null) { // 方法上没有，再看整个 Controller 上有没有
                requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
            }
            if (requireRole != null) {
                Object roleAttr = request.getAttribute("ctx.role");
                int role = (roleAttr instanceof Integer i) ? i : -1;
                // TODO(源) 写一句：这里为什么用 role < 需要值，而不是 role != 需要值？
                if (role < requireRole.value()) {
                    writeJson(response, HttpServletResponse.SC_FORBIDDEN, 403,
                            "无权限：需要角色 " + requireRole.value() + "，当前角色 " + role);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 自己写 401 响应。preHandle 返回 false 后框架不会替我们写响应，
     * 不写的话前端收到的是 200 + 空 body。
     */
    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, 401, message); // TODO(源) 这个常量等于几？
    }

    /** 401 和 403 都要"自己写响应"，所以抽成一个方法。 */
    private void writeJson(HttpServletResponse response, int httpStatus, int code, String message) throws IOException {
        response.setStatus(httpStatus);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + code + ",\"message\":\"" + message + "\"}");
    }
}
