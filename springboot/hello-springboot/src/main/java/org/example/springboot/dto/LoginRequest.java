package org.example.springboot.dto;

/** 登录请求体。record 自动生成构造器/访问器/equals，只用来传数据最合适。 */
public record LoginRequest(String username, String password) {
}
