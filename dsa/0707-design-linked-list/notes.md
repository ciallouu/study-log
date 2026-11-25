# 707. 设计链表

https://leetcode.cn/problems/design-linked-list/

## 思路：单链表 + 哨兵（dummy）

**只有两个字段**：`dummy`（哨兵）和 `size`（真实节点数）。

```java
private Node dummy = new Node(0);   // dummy.next 才是真正的头结点
private int size = 0;
```

**没有 `head`、没有 `tail`**——这是本题最关键的取舍。有 dummy 之后：

- `head` 就是 `dummy.next`
- 尾部就是「从 dummy 走 size 步」
- 删头、删中、删尾变成同一段代码

## 四条判定（题目约定，必须背下来）

| 情况 | 正确行为 |
|---|---|
| `addAtIndex` 且 `index > size` | **什么都不做**（不报错、不插尾部） |
| `addAtIndex` 且 `index < 0` | 当作头插（`index = 0`） |
| `addAtIndex` 且 `index == size` | 插到尾部（等价 `addAtTail`） |
| `get` / `deleteAtIndex` 越界 | `get` 返回 `-1`，`deleteAtIndex` 什么都不做，**都不抛异常** |

> 注意：`get` 越界返回 `-1` 而不是抛 `IndexOutOfBoundsException`——**题目约定优先于自己的设计习惯**（和手写 `MyLinkedList` 那版不同）。

## 三个方法塌缩成一句

```java
public void addAtHead(int val) { addAtIndex(0, val); }
public void addAtTail(int val) { addAtIndex(size, val); }
```

删尾也不需要特殊处理：`prev.next = prev.next.next` 在最后一个节点时，`prev.next.next` 就是 `null`，赋值过去自然接上。**所以不需要维护 tail**。

## 复杂度

- `get` / `addAtIndex` / `deleteAtIndex`：O(index)，最坏 O(n)
- `addAtHead`：O(1)；`addAtTail`：O(n)（单链表没有尾指针）
- 空间 O(n)（存储 + 一个哨兵）

## 踩坑记录（本人实际踩过的）

1. **把 `Node`、`dummy`、`size` 写进了构造器里** → 成员（字段、内部类）必须在**类体**中，构造器里只能写初始化动作
2. **残留了昨晚 head/tail 版本的字段**，导致 8 处「找不到符号」——混用「无哨兵」和「有哨兵」两套写法
3. `index == size - 1` 写成「插到尾部」→ 应为 `index == size`
4. **漏了 `index > size → return`** 这条守卫
5. 守卫语后忘了 `return`，代码继续往下执行两遍（`void` 方法只能写 `return;`，不能 `return -1;`）

## 一句话总结

> 哨兵节点的全部价值：**把边界情况变成普通情况**。代价是多一个节点，收益是少一半分支。
