package org.example.springboot.config;

import org.example.springboot.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    // 构造器注入：和 AuthService 注入 UserDao 完全一样的套路
    public WebMvcConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)   // ← 只拦"谁"，不写"拦什么逻辑"
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");
    }
}