# MySQL 索引实验记录

> 实验日期：2026-11-21　环境：MySQL 8.3　库：`campus_doc`　表：`document`
> 数据量：4 行原始数据 + 800 行测试数据 = 804 行

---

## 一、为什么需要索引

列表页的核心查询：

```sql
SELECT * FROM document WHERE category_id = 1 AND audit_status = 1;
```

- **无索引**：从第 1 行扫到第 804 行 → `type = ALL`（全表扫描）
- **有索引**：直接定位到 `category_id=1` 的位置 → `type = ref`

**索引就是书的目录：不读正文，先读目录找到页码。**

---

## 二、B+ 树：为什么是它

### Q1：为什么不用二叉树 / 红黑树？

树太高。100 万数据，二叉树高度约 20 层 = **20 次磁盘 IO**。磁盘 IO 比内存慢约 10 万倍，这是决定性开销。

### Q2：为什么 B+ 树优于 B 树？

| | B 树 | B+ 树 |
|---|---|---|
| 非叶子节点 | 存索引键 **+ 数据** | **只存索引键** |
| 单页能装的键 | 少 | **多（扇出大）** |
| 树高 | 高 | **矮** |

B+ 树 3~4 层即可支撑千万级数据 → 3~4 次 IO，而 B 树可能要 5~6 次。

### Q3：为什么叶子节点用双向链表串联？

**范围查询和排序**。`BETWEEN`、`>`、`ORDER BY` 找到起点后顺着链表扫即可，不用回上层节点绕。

### 三个结论

> **B+ 树的优势 = 树矮（IO 少）+ 能范围扫 + 天生有序**

### 主键为什么建议自增

InnoDB 数据按主键顺序存放。随机主键（如 UUID）会让插入位置到处乱跳 → 频繁**页分裂** → 碎片、写性能下降。自增主键永远追加在末尾。

---

## 三、InnoDB 的两种索引与「回表」

| | 聚簇索引（PRIMARY） | 二级索引（idx_xxx） |
|---|---|---|
| 叶子节点存什么 | **整行数据** | **索引列 + 主键值** |
| 一个表能有几个 | 1 个（主键） | 多个 |
| 查询代价 | 一次查找 | 可能需要**回表** |

### 回表是什么

```sql
SELECT * FROM document WHERE category_id = 1;
```

1. 在 `idx_category_status` 里找到 `category_id=1` → 拿到主键 `id=1`
2. 索引里**没有** `title`、`file_path` 等列
3. 拿 `id=1` 回聚簇索引再查整行 ← **这一步叫回表**

### 覆盖索引（避免回表）

```sql
SELECT id, category_id FROM document WHERE category_id = 1;
```

要的列全在索引里 → **不需要回表** → `Extra: Using index`

> **实践**：列表页别写 `SELECT *`，只取需要的字段；高频字段可以加进索引做覆盖索引。

---

## 四、EXPLAIN 五列怎么读

```sql
EXPLAIN SELECT ...\G      -- 用 \G 竖排输出，命令行表格不会错位
```

### 1. `type`：用什么方式找数据

| 值 | 含义 | 触发条件 |
|---|---|---|
| `const` | 最多一行 | **主键或唯一索引**的等值查询 |
| `ref` | 走索引等值，可能多行 | **普通索引**的等值查询 |
| `range` | 走索引扫一段 | `>` `<` `BETWEEN` `LIKE 'x%'` `IN` |
| `ALL` | **全表扫描** | 无索引或索引失效 |

性能排序：`const` > `eq_ref` > `ref` > `range` > `index` > **`ALL`**

### 2. `key`：实际用了哪个索引

**填索引名**（`PRIMARY`、`idx_xxx`），不是字段名。`NULL` = 没用索引。

### 3. `key_len`：索引真正用了几列（**最关键的一列**）

算法：**把参与「查找」的索引列的字节数相加**。

| 字段 | 类型 | 字节 |
|---|---|---|
| `category_id` | INT UNSIGNED | 4 |
| `audit_status` | TINYINT | 1 |
| `uploader_id` | INT UNSIGNED | 4 |
| `created_at` | DATETIME | 5 |
| `title` | VARCHAR(128) utf8mb4 | 128×4+2 = **514** |

所以：

| key_len | 含义 |
|---|---|
| 4 | 只用了 `category_id` |
| 5 | 用了 `category_id` + `audit_status` |
| 9 | 用了 `category_id` + `created_at` |
| 514 | `title` 全长度 |

> 进阶：字段允许 `NULL` 时每列多 1 字节（本表都是 `NOT NULL`，所以干净）。

### 4. `Extra`：附加信息

| 值 | 含义 |
|---|---|
| `NULL` | 没有额外动作，索引把活干完了 |
| `Using index` | **覆盖索引**，不回表 |
| `Using index condition` | **索引下推（ICP）**：条件下推到索引层过滤，减少回表 |
| `Using where` | 回表后还要再过滤 |
| `Using filesort` | **需要额外排序**（要优化） |
| `Backward index scan` | 反向扫索引（MySQL 8.0+），`ORDER BY ... DESC` 免 filesort |

### 5. `ref`：比较的值从哪来（**和 type 不是一回事**）

| 值 | 含义 |
|---|---|
| `const` | 常量字面量（`WHERE category_id = 1` 里的 1） |
| `db.table.column` | 来自另一张表的列（JOIN 时） |
| `NULL` | 不是等值查找（`range` / `ALL`） |

---

## 五、12 条实验对照表（今天全部跑过）

| # | SQL 条件 | type | key | key_len | Extra | 结论 |
|---|---|---|---|---|---|---|
| ① | `id = 1` | `const` | PRIMARY | 4 | NULL | 主键最快 |
| ② | `category_id=1 AND audit_status=1` | `ref` | idx_category_status | **5** | NULL | 两列都用上 |
| ③ | 只 `audit_status=1` | **`ALL`** | NULL | NULL | Using where | **最左前缀失效** |
| ④ | `SELECT id, category_id WHERE category_id=1` | `ref` | idx_category_status | 4 | **Using index** | 覆盖索引 |
| ⑤ | `title LIKE '2024%'` | `range` | idx_title | 514 | Using index condition | 前缀可走索引 |
| ⑥ | `title LIKE '%期末%'` | **`ALL`** | NULL | NULL | Using where | **前置 % 失效** |
| ⑦ | `category_id>0 AND audit_status=1` | `range` | idx_category_status | **4** | Using index condition | **范围在前，第二列失效** |
| ⑧ | `SELECT id,created_at WHERE category_id=1 AND created_at>'2028-01-01'` | `range` | idx_category_time | **9** | Using where; **Using index** | 两列都用 + 覆盖索引 |
| ⑨ | `category_id=1 ORDER BY created_at DESC`（加索引前） | `ref` | idx_category_status | 4 | **Using filesort** | 要额外排序 |
| ⑩ | 同上（加 `idx_category_time` 后） | `ref` | idx_category_time | 4 | **Backward index scan** | **免排序** |
| ⑪ | `category_id=1 AND created_at>'2026-01-01'`（804 行全满足） | `ref` | idx_category_status | 4 | Using where | **选择性问题** |
| ⑫ | `category_id=1 AND created_at>'2028-01-01'`（仅 4 行满足） | `range` | idx_category_time | **9** | Using index condition | **选择性高才用** |

### ⑪ vs ⑫：今天最重要的一组对照

| 条件 | 命中行数 | key_len | 说明 |
|---|---|---|---|
| `created_at > '2026-01-01'` | 804 / 804 = **100%** | **4** | 过滤不掉任何行 → 索引第二列毫无价值 |
| `created_at > '2028-01-01'` | 4 / 804 = **0.5%** | **9** | 过滤掉 99.5% → 第二列大显身手 |

> **索引能不能帮上忙，不取决于"你有没有写这个条件"，而取决于"这个条件能过滤掉多少行"。**

---

## 六、索引失效的 6 种场景

| # | 场景 | 例子 | 原因 |
|---|---|---|---|
| 1 | **违反最左前缀** | 索引 `(a,b)`，查 `WHERE b=1` | B+ 树按 (a,b) 排，没有 a 就无起点 |
| 2 | **前置 `%` 模糊** | `LIKE '%关键词%'` | 无法定位起始位置 |
| 3 | **范围条件在前** | 索引 `(a,b)`，查 `WHERE a>0 AND b=1` | a 是范围扫描，a 之后无序，b 无法定位 |
| 4 | **选择性太低** | `WHERE created_at > '1970-01-01'` | 过滤不掉行，用索引不如全表扫 |
| 5 | **对索引列使用函数/运算** | `WHERE YEAR(created_at)=2026`<br>`WHERE id+1=10` | 索引存的是原始值，运算后无法匹配 |
| 6 | **隐式类型转换** | `phone` 是 VARCHAR，查 `WHERE phone = 13800000001`（不加引号） | 数据库对列做类型转换，等价于套了函数 |

### 场景 5 的改写方法

```sql
-- ❌ 失效
WHERE YEAR(created_at) = 2026

-- ✅ 改成范围，走索引
WHERE created_at >= '2026-01-01' AND created_at < '2027-01-01'
```

### 场景 6 的说明

`VARCHAR` 列用数字去查，MySQL 会把**列**转成数字再比较 → 索引失效。
**规则：字段是什么类型，条件值就用什么类型**（字符串加引号）。

---

## 七、索引设计流程

### 4 步法

1. **列出真实的高频查询**（按接口来，不按想象）
2. **看 WHERE：等值条件放前面，范围条件/排序字段放后面**
3. **看 ORDER BY：让索引顺序和排序需求一致**（避免 filesort）
4. **看 SELECT：常用字段能覆盖就做成覆盖索引**

### 索引不是越多越好

每个索引的代价：
- **写成本**：每次 INSERT/UPDATE/DELETE 都要同步维护所有索引
- **空间**：占磁盘
- **优化器负担**：可选索引太多，可能选错

> **规则：按真实查询建索引，不按「可能有用」建。**

### 项目 A 的实际选择

真实列表页查询：

```sql
SELECT * FROM document
WHERE category_id = ? AND audit_status = 1
ORDER BY created_at DESC
LIMIT 20;
```

**最优索引：`(category_id, audit_status, created_at)`**

| 列 | 作用 |
|---|---|
| `category_id` | 等值条件，选择性高，放最左 |
| `audit_status` | 等值条件（通常值固定为 1） |
| `created_at` | 提供排序，免 filesort |

一个索引同时解决 **过滤 + 排序**，且能配合 `LIMIT 20` 提前停止扫描。

---

## 八、面试话术（8 条，可直接背）

1. **为什么用 B+ 树**：非叶子节点不存数据 → 扇出大 → 树矮（3-4 层撑千万级）→ IO 少；叶子链表支持范围查询和排序。
2. **聚簇索引和二级索引的区别**：聚簇索引叶子存整行，二级索引叶子存索引列 + 主键 → 查非索引列要回表。
3. **什么是回表，怎么避免**：二级索引拿到主键后再查聚簇索引；用覆盖索引（SELECT 的列都在索引里）避免。
4. **最左前缀原则**：复合索引必须从最左列开始用；跳过最左列则索引失效。
5. **为什么范围查询后面的列失效**：范围扫描破坏了后续列的有序性，无法继续用索引定位。
6. **怎么优化 ORDER BY**：建包含排序字段的复合索引，让索引顺序等于排序需求，消除 filesort；MySQL 8 还能反向扫描索引满足 DESC。
7. **索引为什么有时不生效**：选择性太低（过滤不掉行）、违反最左前缀、前置 `%`、对列用函数、隐式类型转换。
8. **索引是不是越多越好**：不是。每个索引都会增加写成本和空间占用，按真实查询建。

---

## 九、自测题（能答出来就算掌握了）

1. `key_len = 9` 意味着用了哪几个字段？为什么不是 5？
2. 索引 `(a, b)`，`WHERE a > 1 AND b = 2` 能用几列？为什么？
3. `WHERE created_at > '1970-01-01'` 为什么不用索引？如果改成 `> '2028-01-01'` 呢？
4. `SELECT *` 和 `SELECT id, created_at` 在 `Extra` 里有什么差别？为什么？
5. `WHERE YEAR(created_at) = 2026` 怎么改造成能走索引的写法？
6. 项目 A 的列表页（按分类 + 只看通过 + 按时间倒序）该建什么索引？
