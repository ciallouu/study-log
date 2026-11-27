package org.example.springboot.interceptor;

import io.jsonwebtoken.JwtException;
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
        System.out.println("preHandle 被调用执行");

        // ① 头名照 HTTP 规范写，大小写不敏感（Tomcat 内部会把头名规范化）,
        String header = request.getHeader("Authorization");

        // TODO(源) 这里写一句：为什么这两种情况都要拒绝？
        if (header == null || !header.startsWith("Bearer ")) {
            writeUnauthorized(response, "未登录或凭证缺失");
            // TODO(源) 写一句：return false 之后，Controller 还会被调用吗？
            return false;
        }

        // ③ 验签：签名不对 / 过期 / 格式错，一律 401
        String token = header.substring(7);
        try {
            jwtUtil.parse(token);

            // TODO(源) 问题1：parse() 成功后返回的是 Claims 对象，
            //        里面有什么？有user.getId(), user.getUsername(), user.getRole()
            //        从parse路径查询,返回的对象Claims对象值为
            //        Jwts.parser()
            //                .verifyWith(key)
            //                .build()
            //                .parseSignedClaims(token)
            //                .getPayload();它具体返回的是什么又怎么跟
            //                JwtUtil.createToken联系上的（提示：去 JwtUtil.createToken 看往里面塞了什么）

        } catch (JwtException | IllegalArgumentException e) {
            // TODO(源) 问题2：为什么这里要接 IllegalArgumentException？
            //  JwtException这是jwt密钥没有验证成功抛出的异常?还是指什么IllegalArgumentExceptionnull 是空串的异常报错
            //        举一个具体的 token 值，会走到这个分支。我猜测的举例1 token=null2.token不符合密钥值或者token过期,顺带解释一下token的概念,我听过一种是词元
            writeUnauthorized(response, "凭证无效或已过期");
            return false;
        }

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
