package org.example.springboot.dto;

/** 注册请求体。phone 允许为 null。 */
public record RegisterRequest(String username, String password, String phone) {
}
