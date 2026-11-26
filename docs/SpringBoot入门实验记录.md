# Spring Boot 入门实验记录（2026-11-26）

> 目的：把三个"听过但没见过"的概念，变成自己亲眼看到的证据。
> 项目：`D:\yuan.aiAgent\study-log\springboot\hello-springboot`（Spring Boot 4.2.0 / Spring Framework 7.1.0 / JDK 21）

---

## 实验一：证明 Bean 是 Spring new 的，而且只 new 一次

**改了什么**：在 `AuthService` 和 `UserDao` 的构造器里各加一行打印。

```java
// AuthService.java 构造器
public AuthService(UserDao userDao, JwtUtil jwtUtil) {
    this.userDao = userDao;
    this.jwtUtil = jwtUtil;
    System.out.println(">>> AuthService 被创建了");
}

// UserDao.java 构造器
public UserDao(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
    System.out.println(">>> UserDao 被创建了");
}
```

**观察结果**（2026-11-26 16:25 实测）：

```
>>> UserDaoXXX 被创建了
AuthService>>> XXX 被创建了
2026-11-26T16:25:17 INFO ... Tomcat started on port 8080
```

两行都出现在 **Tomcat 启动之前**；之后反复访问 `/api/users`、`/api/auth/login`，**不再打印**。

**结论**：
- 打印发生在**启动时**，不是第一次请求时 → 容器启动即创建（饿汉式），不是懒加载
- 访问多次接口后**不再打印** → 全项目只有一个对象 → **单例（singleton）**
- **打印顺序暴露依赖顺序**：`UserDao` 先于 `AuthService` —— 因为要造 AuthService 必须先有 UserDao（构造器要它），容器会自动先造依赖
- 推论：`@Service` / `@Repository` / `@Component` 标的东西都是单例，**不能在里面存"当前请求的用户"这类状态**（多线程共享会串号）

---

## 实验二：证明 JSON 是"装箱"出来的，String 不是

**加的两个方法**（写在 `HelloController` 里，它有 `@RestController`）：

```java
@GetMapping("/probe/text")
public String probeText() {
    return "ok";
}

@GetMapping("/probe/json")
public java.util.Map<String, Object> probeJson() {
    return java.util.Map.of("msg", "ok", "code", 200);
}
```

**观察结果**（2026-11-26 16:45 实测，`curl.exe -i` 抓的原始响应头）：

| 地址 | Content-Type | 浏览器正文 |
|---|---|---|
| `/probe/text` | `text/plain;charset=UTF-8` | `ok`（Content-Length: 2） |
| `/probe/json` | `application/json` | `{"msg":"ok","code":200}`（Content-Length: 23） |

**结论**：
- 返回对象 → Jackson 装箱成 JSON，`Content-Type: application/json`
- 返回 String → 原样吐出去，`Content-Type: text/plain`，**不装箱**
- `@RestController` = 每个方法默认 `@ResponseBody` = 默认装箱

---

## 实验三：HTTP 方法参与"查路由表"

**零代码改动**：浏览器直接访问 `http://localhost:8080/api/auth/login`（GET），而这个方法只接受 POST。

**预测**：可能看到 500 + `HttpRequestMethodNotSupportedException: Request method 'GET' is not supported`，因为兜底 `@ExceptionHandler(Exception.class)` 会抢先接住它，把本该是 **405 Method Not Allowed** 的结果盖成 500。

**实际结果**（2026-11-26 16:30 实测）：

```json
{"code":500,"message":"HttpRequestMethodNotSupportedException: Request method 'GET' is not supported"}
```

**预测命中**：本该是 `405 Method Not Allowed`，被兜底 `@ExceptionHandler(Exception.class)` 吃掉，打成了 500。

**缺陷与修法**：
```java
@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
public ResponseEntity<Map<String, Object>> handleMethod(HttpRequestMethodNotSupportedException e) {
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(body(405, "不支持的请求方法: " + e.getMethod()));
}
```
同理还有 415（媒体类型不支持）、406（不可接受）。
**原则：兜底 handler 只接"真正没人管的异常"，不能一锅端 Spring 的 4xx 语义。**

---

## 三个空（2026-11-26 自测定位）

| 空 | 一句话答案 | 状态 |
|---|---|---|
| 谁把对象变成 JSON | `HttpMessageConverter`（Jackson 实现），`@RequestBody` 拆箱 / `@RestController` 装箱 | 实验中 |
| 谁 new 的 Bean | Spring 容器扫描注解 → 反射造对象 → 存进容器 → 按构造器参数注入（依赖注入） | 实验中 |
| 请求怎么找到方法 | `DispatcherServlet` 启动时把所有 `@XxxMapping` 抄成"路径 → 方法"表，请求来了查表 + 反射调用 | 实验中 |
