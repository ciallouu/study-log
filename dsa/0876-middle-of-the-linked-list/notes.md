# 876. 链表的中间结点

https://leetcode.cn/problems/middle-of-the-linked-list/

## 思路：快慢指针（fast 两步 / slow 一步）

```java
ListNode slow = head, fast = head;
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
}
return slow;
```

## 为什么 `while` 里是**两个条件**、且顺序不能反

```java
while (fast.next != null && fast != null)   // ❌ 空指针
```

`&&` 是**短路求值**：左边 false 时右边不执行。所以**可能为 null 的那个判断必须放最左边**。
`fast` 已经是 null 时先算 `fast.next` → `NullPointerException`。

## 为什么**不需要 dummy**

- dummy 的用途是「**把边界情况变成普通情况**」，主要出现在**要改链表结构**的地方（插入/删除，因为有前驱问题）。
- 876 只**走指针、不改结构**，没有前驱问题 → dummy 毫无用处。
- 判据一句话：**看你要不要动 `next` 指针。** 707/203 要动 → 用 dummy；876/234 的找中点部分不动 → 不用。

## 偶数长度为什么返回「后中点」

`[1,2,3,4]`：fast 走 1→3，slow 走 1→2，此时 `fast.next == null` 退出，`slow` 停在 2 → 正好是 LeetCode 要求的「第二个中间结点」。

## 复杂度

- 时间 O(n)
- 空间 O(1)

## 举一反三

- **234 回文链表**：直接复用本函数找中点，再反转后半段比对（本题是它的第一块积木）
- 141/142 环形链表：同一套快慢指针，改成「相遇」判断
- 19 倒数第 N：改成「定差 n+1」的前进方式

## 一句话总结

> 快慢指针的**倍数和初始差**决定了结束时 slow 停在哪；先想「我要停在哪里」，再决定 fast 走几步。
