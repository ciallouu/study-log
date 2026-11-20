# Java 面向对象（C++ 迁移版）

> 前提：C++ 学过类、继承、虚函数、多态。所以本文不重讲概念，只讲 **Java 的差异** + **你要自己写的 5 个 demo**。

---

## 一、C++ → Java 关键差异（这 12 条决定你少走多少弯路）

| # | C++ | Java | 影响 |
|---|---|---|---|
| 1 | 有指针、栈对象 | **只有引用**，对象全在堆上，由 GC 回收 | 不用管 delete/析构，也别指望 `finalize()` |
| 2 | `virtual` 显式声明虚函数 | **实例方法默认可重写**（除 `final` / `static` / `private`） | C++ 里习惯加 virtual，Java 里要习惯"默认就是虚" |
| 3 | 支持多继承 | **类单继承，接口可多实现** | 多态靠接口，不靠多继承 |
| 4 | 有析构函数 | **没有析构函数**，用 `try-with-resources` / `AutoCloseable` 管理资源 | 文件、连接要显式关闭 |
| 5 | 有 `const` 成员函数 | **没有**，用 `final` 字段表达不可变 | |
| 6 | 引用传参 | **只有值传递**；对象传的是"引用的副本" | 方法内改对象属性会影响外部，改引用本身不会 |
| 7 | 访问控制 public/protected/private | 多一个 **default（包内可见）**，不写就是它 | 初学者最常忽略的一档 |
| 8 | 全局函数 | **一切都在类里**，`static` 方法相当于"类级函数" | |
| 9 | 模板（编译期展开） | **泛型是类型擦除**（运行时没有 T） | 不能 `new T()`、不能对泛型做 `instanceof` |
| 10 | 字符串是字符数组 | `String` **不可变**；拼接用 `StringBuilder` | 循环里用 `+` 拼字符串是性能坑 |
| 11 | 无统一根类 | **`Object` 是所有类的根**，自带 `equals` / `hashCode` / `toString` | 重写 `equals` 必须同时重写 `hashCode` |
| 12 | 没有注解/反射 | **注解 + 反射**是框架的基础 | Spring 的 IOC/AOP 全靠这两个，先知道存在 |

---

## 二、今天要自己写的 5 个 Demo

> 规则：**每个 demo 先自己写，卡住 20 分钟再看资料。** 写完故意改坏一处（比如去掉 `@Override`、把父类方法改成 `static`），看看报什么错——这是最快的理解方式。

### Demo 1｜封装（约 20 分钟）
`Student` 类：
- 字段全部 `private`：`name`、`age`、`studentId`
- 提供 getter/setter，其中 `setAge` 要做校验：`age < 0 || age > 150` 抛 `IllegalArgumentException`
- 一个无参构造 + 一个全参构造（**构造器重载**）
- `toString()` 输出可读信息

**验收**：`new Student(-5)` 那类非法数据进不去对象。

### Demo 2｜继承与重写（约 20 分钟）
- `Person` 父类：`name`、`protected void introduce()`
- `Student extends Person`、`Teacher extends Person`，各自重写 `introduce()`，用 `super.introduce()`
- 两个子类的构造器里 `super(name)` 必须是**第一行**

**验收**：能说清"构造器不能被继承"这句话的含义。

### Demo 3｜多态（约 30 分钟，今天最重要）
- `Person[] people = { new Student("张三"), new Teacher("李老师") }`
- 循环调用 `people[i].introduce()`，确认**运行时**才决定调哪个版本
- 加一个 `if (people[i] instanceof Student s)`（Java 16+ 模式匹配）访问子类特有方法
- **故意试一次**：用 `Person` 类型引用去调 `Student` 特有方法 → 观察编译错误

**验收**：能解释"编译时类型 vs 运行时类型"，以及为什么父类引用调不到子类特有方法。

> **为什么这个最重要**：Spring 的 IOC 容器本质就是"根据配置创建子类对象，赋给接口引用"。多态不懂，Spring 就只能背注解。

### Demo 4｜抽象类 vs 接口（约 25 分钟）
同一个需求，两种建模：
- `Shape` **抽象类**：有字段 `name`，有具体方法 `describe()`，有抽象方法 `area()`
- `Payable` **接口**：只有 `pay(double amount)` 这一个能力
- 让 `Circle` 继承 `Shape`，同时让 `Employee` 实现 `Payable`

**选型口诀**：
- 抽象类表达 **is-a**（有什么），可以有字段和共用实现，只能单个继承
- 接口表达 **can-do**（能做什么），可以多个实现

### Demo 5｜综合：图书管理雏形（约 30 分钟，项目 A 的地基）
- 接口 `BookService`：`add(Book)`、`findById(int)`、`listAll()`、`remove(int)`
- 实现类 `MemoryBookService`：用 `ArrayList<Book>` 存
- 自定义异常 `BookNotFoundException extends RuntimeException`
- `Main` 类里**只面向接口编程**：`BookService service = new MemoryBookService();`

**验收**：把 `MemoryBookService` 换成 `FileBookService`（用文件存），`Main` 一行都不用改。

做完这个，你就理解了为什么后端代码要"面向接口编程"——因为明天换数据库、下周换缓存，上层都不用动。

---

## 三、5 个自测问题（答不出 = 今天没学会）

1. 重载（overload）和重写（override）的区别？各自的判定条件是什么？
2. `static` 方法能被重写吗？为什么？
3. 父类引用指向子类对象时，能调用子类**特有**的方法吗？为什么？
4. 为什么重写 `equals` 必须同时重写 `hashCode`？（提示：HashMap 怎么找 key）
5. 抽象类和接口该怎么选？各举一个你项目里的例子。

---

## 四、今天就记住的 5 个面试坑

1. **`@Override` 一定要写**：不写不会报错，但方法名拼错时会悄悄变成"新方法"，bug 极难查
2. **构造器不继承**，`super()` 必须是子类构造器的第一行
3. **父类引用不能调用子类特有方法**，需要向下转型 + `instanceof` 判断
4. **`equals` 和 `hashCode` 必须成对重写**，否则对象放进 `HashMap`/`HashSet` 会"失踪"
5. **`String` 不可变**，循环拼接用 `StringBuilder`

---

## 五、今天不写 `package` 声明

先用默认包（文件放同一目录），把注意力集中在概念上。等做项目 A 时再引入 `package com.ciallou.xxx`，那时才有必要。
