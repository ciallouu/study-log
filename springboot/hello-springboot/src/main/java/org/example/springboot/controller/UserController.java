package org.example.springboot.controller;

import org.example.springboot.dao.UserDao;
import org.example.springboot.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 第一块验证：Spring Boot 能不能真的读到 MySQL 里的数据。
 * 启动后访问 http://localhost:8080/api/users
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping
    public List<User> list() {
        return userDao.findAllForList();
    }
}
