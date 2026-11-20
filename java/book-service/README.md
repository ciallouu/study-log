# 图书服务（Demo 5）—— 面向接口编程雏形

Java 面向对象第 5 个练习，也是项目 A（校园资料共享平台）的地基。

## 文件

| 文件 | 说明 |
|---|---|
| `Book.java` | 实体。构造器校验（fail-fast）+ `toString`，无 setter（不可变） |
| `BookService.java` | **接口**：`add` / `findById` / `listAll` / `remove` |
| `BookNotFoundException.java` | 自定义异常，`extends RuntimeException` |
| `MemoryBookService.java` | 实现类，`ArrayList` 存储 |
| `Demo05.java` | 调用方，**只依赖 `BookService` 接口** |

## 验收标准

> `Demo05` 里除了 `new MemoryBookService()` 那一行，不出现 `MemoryBookService`。

满足这条 = 上层代码与具体实现解耦，将来换 `FileBookService` / `DatabaseBookService`，`Demo05` 一行不改。

## 接口设计要点

| 方法 | 设计选择 | 理由 |
|---|---|---|
| `void add(Book)` | 返回 void，失败抛异常 | 新增不需要回传；id 重复属异常情况 |
| `Book findById(int)` | 找不到返回 `null` | **查询**失败是正常结果，不该用异常 |
| `List<Book> listAll()` | 返回 `List` 而非 `ArrayList` | 面向接口编程也要用在返回值上，不泄露实现 |
| `Book remove(int)` | 返回被删对象，不存在抛异常 | **操作**失败属异常情况 |

**通用原则：查询失败返回 null / 空集合；操作失败抛异常。**（对应 REST 的 404 与 400）

## 踩的坑

1. **自定义异常没写 message 构造器** → `e.getMessage()` 返回 `null`，排障时毫无信息
2. **构造器不会被继承**：`class X extends RuntimeException {}` 只隐式获得无参构造器，想要 `new X("msg")` 必须自己声明
3. **一旦声明了带参构造器，编译器就不再提供默认无参构造器**，原有无参调用处会编译失败
4. **删除时不能用增强 for**：底层是 Iterator，`list.remove()` 会让 `modCount` 变化，下次 `next()` 抛 `ConcurrentModificationException`。用**下标循环**或 `Iterator.remove()`
5. **`listAll` 必须返回副本**：直接 `return books;` 会让调用方一句 `clear()` 清空内部数据（防御性复制）
6. `add` 里复用 `findById` 判断重复，避免同一逻辑写两遍

## 待改进（记录，不在本练习范围）

- `price` 用 `double` 存金额有精度问题（`0.1 + 0.2 = 0.30000000000000004`），真实项目用 `BigDecimal` 或"以分存储的整数"
- 无并发控制；无持久化
