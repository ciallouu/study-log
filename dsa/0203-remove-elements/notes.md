# 203. 移除链表元素

https://leetcode.cn/problems/remove-linked-list-elements/

## 思路：dummy 锚点 + prev 游标

两个变量分工**必须分清**：

- `dummy`：**锚点（钉子）**，创建后永不移动，唯一作用是 `return dummy.next` 时能找回头
- `prev`：**游标**，唯一会移动的指针

```java
while (prev.next != null) {
    if (prev.next.val == val) {
        prev.next = prev.next.next;   // 删除时 prev 不动
    } else {
        prev = prev.next;             // 不删时才前进
    }
}
```

## 核心规则（一句话）

> **删除时 `prev` 不动，不删时 `prev` 才前进。**

因为删完之后，新的 `prev.next` 还没被检查过，必须原地再看一眼。这就是为什么必须写成 `if / else`，而不是「if 处理完再统一前进」。

## 踩坑记录（本人实际踩过）

写成「if 块里处理，块外统一 `prev = prev.next`」→ **漏检**。

以 `[1, 2, 2, 1]`、`val = 2` 为例：

| 步骤 | 动作 | 链表状态 |
|---|---|---|
| 初态 | prev 指向 dummy | `1 → 2 → 2 → 1` |
| 1 | 1≠2，prev 前进到 1 | 不变 |
| 2 | prev.next=2 命中，删掉 | `1 → 2 → 1` |
| 3 | **块外又前进** → prev 跳到 1 | 中间那个 2 从未被检查 |

还有一个更严重的错误：**把 `dummy` 当游标一起移动** `dummy = dummy.next` → `return dummy.next` 返回的已经不是头结点，且循环终止条件跟着漂移。

## 复杂度

- 时间 O(n)
- 空间 O(1)

## 递归写法（了解即可）

```java
public ListNode removeElements(ListNode head, int val) {
    if (head == null) return null;
    head.next = removeElements(head.next, val);   // ① 先让「我后面」干净
    return head.val == val ? head.next : head;    // ② 再决定「我」留不留
}
```

- 递归**天然不需要 dummy**：「删头」这件事由上一层的调用者完成
- 空间 O(n)（递归栈），链表长会爆栈
- **面试先写迭代版**，被问「还有别的方法吗」再给这个

## 一句话总结

> dummy 是钉子，不是遥控器。
