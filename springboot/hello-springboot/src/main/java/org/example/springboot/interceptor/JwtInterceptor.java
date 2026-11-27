package org.example.springboot.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springboot.util.JwtUtil;
import org.springframework.stereotype.Component;
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

        // TODO(源) 这里写一句：为什么这两种情况都要拒绝？
        if (header == null || !header.startsWith("Bearer ")) {
            writeUnauthorized(response, "未登录或凭证缺失");
            // TODO(源) 写一句：return false 之后，Controller 还会被调用吗？
            return false;
        }

        // ③ 验签：下午写
        // String token = header.substring(7);
        // jwtUtil.parse(token);

        return true;
    }

    /**
     * 自己写 401 响应。preHandle 返回 false 后框架不会替我们写响应，
     * 不写的话前端收到的是 200 + 空 body。
     */
    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // TODO(源) 这个常量等于几？
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\"}");
    }
}
