package org.example.springboot.dto;

/**
 * 登录成功后返回给前端的东西。
 * token 由前端保存（localStorage），之后每个请求放到 Authorization: Bearer <token>。
 * expiresIn 是秒数，前端可以用它做过期提醒。
 */
public record LoginResponse(String token, String username, int role, long expiresIn) {
}
