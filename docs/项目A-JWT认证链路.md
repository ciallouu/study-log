# 项目 A：JWT 认证链路（已完成到「认人」）

> 最后更新：2026-11-27
> 代码位置：`study-log/springboot/hello-springboot/`

## 一、整条链路（四层，前三层已完成）

```
① 造票    POST /api/auth/login
          AuthController.login → AuthService.login（查库 + BCrypt.matches）
          → JwtUtil.createToken（sub/username/role + iat/exp + 签名）
          → 返回 207 字符 token         ← 全程不写数据库、不建 session

② 建门    WebMvcConfig.addInterceptors
          addPathPatterns("/api/**") + excludePathPatterns("/api/auth/**")
          └─ 只有「拦截范围」和「白名单」两个旋钮

③ 验票    JwtInterceptor.preHandle
          格式关：header == null || !header.startsWith("Bearer ")  → 401 未登录或凭证缺失
          验签关：jwtUtil.parse(token) 抛 JwtException              → 401 凭证无效或已过期

④ 认人    request.setAttribute("ctx.userId"/"ctx.username"/"ctx.role")
          Controller 用 @RequestAttribute("ctx.userId") int userId 取回

⑤ 权限（未做）  按 claims 里的 role 判断能不能访问
```

## 二、关键代码坐标

| 文件 | 关键行 | 作用 |
|---|---|---|
| `interceptor/JwtInterceptor.java` | `:18-19` | `@Component` + `implements HandlerInterceptor` |
| 同上 | `:32` | `request.getHeader("Authorization")`（头名大小写不敏感） |
| 同上 | `:35-39` | 格式关，`return false` 后 Controller **不会**被调用 |
| 同上 | `:47` | `Claims claims = jwtUtil.parse(token)`（一次调用 = 验签 + 解析） |
| 同上 | `:48-50` | 三样身份塞进 request |
| 同上 | `:51-54` | 验签关，`JwtException \| IllegalArgumentException` → 401 |
| 同上 | `:63-67` | 自己写 401 响应（`return false` 后框架不代写） |
| `config/WebMvcConfig.java` | — | `addInterceptors` 挂号，只写"拦谁"，不写"拦什么逻辑" |
| `controller/UserController.java` | `:27-30` | `@RequestAttribute("ctx.userId") int userId` |
| `scripts/verify-identity.ps1` | — | 四条自动化验收 |

## 三、验收记录（2026-11-27 实测）

| # | 场景 | 期望 | 实测 |
|---|---|---|---|
| 1 | 有效票（test01，sub=8） | 200 | **200** ✅ |
| 2 | 不带头 | 401 | **401** ✅ |
| 3 | 假票 `Bearer abc` | 401 | **401** ✅ |
| 4 | 换人（test02，sub=10） | 200 且控制台 id 不同 | **200**，控制台打出 `id = 8` 和 `id = 10` ✅ |

**第 4 条才是「认人」的真证据**：状态码两次都是 200 看不出区别，只有控制台的 userId 不同，才证明身份来自**这张票**而不是写死的常量。

## 四、三个设计决定（面试可讲）

### 1. 服务端不存 token（无状态）

`JwtUtil.createToken()` 里**没有一行数据库操作**，凭证不落库、不建 session。

- 好处：多机天然支持（不用共享 session 表）、重启不丢、无数据库查询开销
- 代价（必须能说出来）：
  - **不能主动注销**——前端删了 token，服务端无感知，票到期前仍可用
  - **改密码后旧票仍有效**——真实安全缺口
  - **权限变更延迟生效**——旧票里的 role 是签发时的
- 当前缓解：`jwt.expire-minutes=120` 压窗口；后续在项目 B 加 Redis 黑名单 / refresh token

一句话：**JWT 让服务端"不存东西也能认出你"，代价是"认出了就没法当场反悔"。**

### 2. 身份用 `request.setAttribute` 传递，不用成员变量

`JwtInterceptor` 是**单例 Bean**（整个应用只有一个对象）。若写成 `private int currentUserId`，并发时必然串号：

```
请求A(test01) → currentUserId = 8
请求B(test02) → currentUserId = 10
请求A 的 Controller 读到 → 10      ← 跨用户数据泄露
```

`HttpServletRequest` 的生命周期 = **一次请求**，A 和 B 是两个对象，天然隔离。

### 3. 401 分两关，message 不同

| message | 产生位置 | 含义 |
|---|---|---|
| `未登录或凭证缺失` | `JwtInterceptor.java:36`（格式关） | 根本没给票 / 不是 Bearer |
| `凭证无效或已过期` | `JwtInterceptor.java:52`（验签关） | 给了票，但签名不对或已过期 |

**排查时先看 message 就知道是哪一关拦的**（只看 401 判断不出来）。

## 五、已知缺口（按优先级）

1. **权限层没做**：任何有效票（role=1 也能）都能调 `/api/users`，role 没有参与判断
2. **405/415/406 被兜底吞成 500**：`@ExceptionHandler(Exception.class)` 抢在 Spring 默认解析器之前接住了它们
3. **500 的 message 直接回前端**：`e.getClass().getSimpleName() + ": " + e.getMessage()` 会泄露表名/SQL 路径，上线前必须按 profile 分流
4. **不能主动注销 / 改密旧票有效**：见设计决定 1

## 六、我自己踩过的坑（素材本口径）

1. **`fast`/起点一变，语义就翻转**（链表 143 同款问题）——JWT 这里对应的是"`fast != null` 从奇数变偶数"
2. **`jwtUtil.parse` 调了两次**——`parse()` 是"验签 + 解析"合一，一次调用就够
3. **`@RequestAttribute` 的 key 是字符串，编译期无法校验**——写错只在运行时抛 `ServletRequestBindingException`（`RequestAttributeMethodArgumentResolver.java:52-59`），被兜底吃成 500
4. **`Authorization: Bearer ` 带尾随空格不会走到空串分支**——Tomcat 按 HTTP 规范裁掉头值首尾空格（OWS），header 变成 `"Bearer"`，在格式关就被拦
