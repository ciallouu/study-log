package org.example.springboot.controller;

import org.example.springboot.annotation.RequireRole;
import org.example.springboot.dao.UserDao;
import org.example.springboot.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 管理员接口（授权演示）。
 *
 * <p>路径 /api/admin/** 会被 JwtInterceptor 拦到（因为它在 /api/** 范围内），
 * 所以进来之前一定已经验过票；能不能进来，看方法上那个 @RequireRole(2)。
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserDao userDao;

    public AdminController(UserDao userDao) {
        this.userDao = userDao;
    }

    /** 只有 role >= 2 的人能看全站用户列表。 */
    @RequireRole(2)
    @GetMapping("/users")
    public List<User> listAll(@RequestAttribute("ctx.role") int role) {
        System.out.println("管理员接口被访问，角色 = " + role);
        return userDao.findAllForList();
    }

    /** 对照组：这个接口没贴注解，任何登录用户都能访问。 */
    @GetMapping("/ping")
    public Map<String, Object> ping(@RequestAttribute("ctx.username") String username) {
        return Map.of("msg", "ok", "user", username);
    }
}
