# 项目 A 接口自测（PowerShell 里逐段粘贴运行）

> 为什么要先注册？数据库里 zhangsan/lisi/admin 三个账号的 password_hash 是
> `$2a$10$abcdefghijklmnopqrstuv` —— 20 个字符的**假密文**（真 BCrypt 是 60 字符），
> 所以拿它们登录一定 401。先用 register 造一个真账号。

## 1. 注册

```powershell
$b = @{ username = 'yuan'; password = 'abc123456'; phone = '13700000001' } | ConvertTo-Json
Invoke-RestMethod http://localhost:8080/api/auth/register -Method Post -ContentType 'application/json' -Body $b
```

预期：无输出（HTTP 201）。再跑一次 → 报 `用户名已存在`（HTTP 409）。

## 2. 登录拿 token

```powershell
$b = @{ username = 'yuan'; password = 'abc123456' } | ConvertTo-Json
$r = Invoke-RestMethod http://localhost:8080/api/auth/login -Method Post -ContentType 'application/json' -Body $b
$r | Format-List
$r.token
```

预期输出 `token / username / role / expiresIn=7200`。

## 3. 故意输错密码

```powershell
$b = @{ username = 'yuan'; password = 'wrongpass' } | ConvertTo-Json
Invoke-RestMethod http://localhost:8080/api/auth/login -Method Post -ContentType 'application/json' -Body $b
```

预期：HTTP 401 + `用户名或密码错误`。

## 4. 看一眼 token 里到底有什么

```powershell
$p = ($r.token -split '\.')[1]
$p = $p.Replace('-', '+').Replace('_', '/')
while ($p.Length % 4) { $p += '=' }
[Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($p))
```

预期：`{"sub":"4","username":"yuan","role":1,"iat":...,"exp":...}`

## 5. 用户列表

浏览器直接开 http://localhost:8080/api/users
