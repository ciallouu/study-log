package org.example.springboot.service;

import org.example.springboot.common.ApiException;
import org.example.springboot.dao.UserDao;
import org.example.springboot.dto.LoginRequest;
import org.example.springboot.dto.LoginResponse;
import org.example.springboot.dto.RegisterRequest;
import org.example.springboot.entity.User;
import org.example.springboot.util.JwtUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证业务：注册、登录、签 token。
 * Controller 只负责收参数和返回，逻辑都放这里 —— 这样以后写单元测试不用启动 Web 容器。
 */
@Service
public class AuthService {

    private final UserDao userDao;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserDao userDao, JwtUtil jwtUtil) {
        this.userDao = userDao;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterRequest req) {
        if (req.username() == null || req.username().isBlank()) {
            throw new ApiException(400, "用户名不能为空");
        }
        if (req.password() == null || req.password().length() < 6) {
            throw new ApiException(400, "密码至少 6 位");
        }
        // BCrypt 每次加盐都不同：同一个密码两次哈希结果完全不一样，这是正常且必须的
        String hash = encoder.encode(req.password());
        try {
            userDao.insert(req.username().trim(), hash, req.phone());
        } catch (DuplicateKeyException e) {
            throw new ApiException(409, "用户名已存在");
        }
    }

    public LoginResponse login(LoginRequest req) {
        if (req.username() == null || req.password() == null) {
            throw new ApiException(400, "用户名和密码不能为空");
        }
        // 注意：用户不存在和密码错误返回同一句话，避免被用来枚举账号
        User user = userDao.findByUsername(req.username().trim())
                .orElseThrow(() -> new ApiException(401, "用户名或密码错误"));

        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new ApiException(401, "用户名或密码错误");
        }

        String token = jwtUtil.createToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getUsername(), user.getRole(), jwtUtil.getExpireSeconds());
    }
}
