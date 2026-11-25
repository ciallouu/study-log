# 19. 删除链表的倒数第 N 个结点

https://leetcode.cn/problems/remove-nth-node-from-end-of-list/

## 思路：dummy + 快慢指针（差值 n+1）

```java
ListNode dummy = new ListNode(0);
dummy.next = head;
ListNode slow = dummy, fast = dummy;

for (int i = 0; i < n + 1; i++) fast = fast.next;   // ① fast 先走 n+1 步

while (fast != null) {                              // ② 同步前进到 fast == null
    fast = fast.next;
    slow = slow.next;
}

slow.next = slow.next.next;                         // ③ slow 正是待删节点的前驱
return dummy.next;
```

## 为什么是 `n + 1` 步（本题唯一的知识点）

把位置编号：`dummy` 记为 0，`null` 记为 `size + 1`。

```
fast 先走 n+1 步 → 停在位置 n+1
fast 与 slow 的距离恒为 n+1
fast 走到 size+1（null）时，slow 在 size+1-(n+1) = size-n
位置 size-n = 倒数第 n+1 个 = 待删节点的「前驱」
```

| 差值 | slow 最终停在 | 结果 |
|---|---|---|
| `n` 步 | 待删节点**本身** | 删不掉——单链表没有 prev 指针 |
| `n + 1` 步 | 待删节点的**前驱** | ✅ 可以 `slow.next = slow.next.next` |

**关键**：slow 要停在**前驱**上，不是停在目标节点上。因为删除只能通过前驱改 `next` 来完成。

## 为什么需要 dummy

`solve` 的边界是「删的正好是头结点」（即 `n == size`）。有 dummy 之后，头结点也有前驱（就是 dummy），**删头退化成普通情况**，不需要单独写 `if`。

## 循环条件写 `while (fast != null)`

不写 `while (fast.next != null)`：`fast != null` 的终止点更干净——走到 null 为止，恰好让 slow 落在前驱。

## 复杂度

- 时间 O(n)：只遍历一次
- 空间 O(1)

## 举一反三

同一套「快慢指针 + 定差」思想：

- 876 链表的中间结点（fast 走 2 步、slow 走 1 步）
- 141 / 142 环形链表（快慢指针相遇 / 找环入口）
- 160 相交链表（两个指针走完自己再走对方，最终等长）

## 一句话总结

> 快慢指针的差值不是随便定的：**差多少步，取决于你最后要停在哪里**（前驱 / 本身 / 中点）。
