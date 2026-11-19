# Git 日常速查

## 每天就三条命令

```cmd
cd /d D:\yuan.aiAgent\study-log
git add .
git commit -m "feat(dsa): 977 有序数组的平方"
git push
```

> 只有第一次推送需要写 `git push -u origin main`，之后 `git push` 就够了。

---

## 完整流程：新做一道题

### 1. 新建文件夹

命名规则：`题号(4位)-英文短名`，例如：

```
dsa/0977-sorted-squares/
├── Solution.java
└── notes.md
```

### 2. 写两个文件

- `Solution.java`：AC 的代码
- `notes.md`：**思路 / 复杂度 / 踩的坑**（这个比代码值钱）

### 3. 提交前先看一眼状态

```cmd
git status
```

确认只有你想要的改动用绿色列出，没有 `target/`、`.idea/`、`*.class` 混进来。

### 4. 提交并推送

```cmd
git add .
git commit -m "feat(dsa): 977 有序数组的平方"
git push
```

---

## commit message 规范

格式：`类型(范围): 一句话说明`

| 类型 | 用在什么时候 | 例子 |
|---|---|---|
| `feat` | 新增内容 | `feat(dsa): 977 有序数组的平方` |
| `fix` | 修 bug / 改错 | `fix(dsa): 修正 27 的越界判断` |
| `docs` | 只改文档 | `docs: 补充 README 目录结构` |
| `refactor` | 重构不改功能 | `refactor(dsa): 704 改用统一二分模板` |
| `chore` | 杂项（配置、gitignore） | `chore: 忽略 IDEA 配置目录` |

**禁忌**：不要写 `update`、`提交`、`1` 这种什么都没说的 message。面试官看 commit 历史就是在看你的工程习惯。

---

## 查看类命令（都很常用）

| 命令 | 作用 |
|---|---|
| `git status` | 当前改了什么（最常用） |
| `git log --oneline` | 提交历史（一行一条） |
| `git log --oneline --graph` | 带分支图的历史 |
| `git diff` | 具体改了哪几行 |
| `git remote -v` | 远端地址对不对 |

---

## IDEA 图形化提交（不用敲命令）

1. `Ctrl + K` → 打开 Commit 面板
2. 勾选要提交的文件 → 填 message → 点 **Commit and Push**
3. `Ctrl + Shift + K` 单独 Push

IDEA 会自动 `git add`，适合日常；命令行适合你想看清到底提交了什么。

---

## 常见问题

| 情况 | 处理 |
|---|---|
| commit message 写错了（还没 push） | `git commit --amend -m "正确的 message"` |
| 漏加了文件 | `git add 漏掉的文件` 再 `git commit --amend --no-edit` |
| 提交了不该提交的文件（如 target/） | `git rm -r --cached target` 然后 commit（`.gitignore` 已配好，通常不会发生） |
| push 报 `fetch first` / `rejected` | 远端有新提交，先 `git pull --rebase origin main` 再 `git push` |
| push 报 `Repository not found` | 检查 remote 里用户名**大小写**是否准确：`git remote -v` |
| push 报 `SSL certificate problem` | `git config --global http.sslBackend schannel` |
| 想撤销还没提交的改动 | `git restore 文件名`（危险，改动会丢） |
| 想看某次提交改了什么 | `git show 提交号` |

---

## 三条铁律

1. **`.gitignore` 已经配好，别用 `git add -f` 强行加 IDE 配置和编译产物。**
2. **永远不要提交密码、密钥、`.env` 文件。** 一旦推到远端，即使删掉也在历史里。
3. **每天至少提交一次。** 连续 30 天的 commit 历史，是双非简历上最便宜的可信度。
