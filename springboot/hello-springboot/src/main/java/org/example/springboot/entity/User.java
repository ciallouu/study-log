package org.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * 对应数据库 user 表。
 * 字段名用驼峰，数据库用下划线，映射在 UserDao 的 RowMapper 里手工做（不靠框架魔法）。
 *
 * JsonInclude(NON_NULL)：值为 null 的字段不参与 JSON 序列化。
 * 这样列表接口里 passwordHash 为 null 时，连字段名都不会出现在响应里。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private Integer id;
    private String username;

    /** 数据库 password_hash，BCrypt 密文，长度 60。对外绝对不能返回这个字段。 */
    private String passwordHash;

    /** 1 = 普通学生，2 = 管理员 */
    private Integer role;

    private String phone;
    private LocalDateTime createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
