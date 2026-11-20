# 206. 反转链表

https://leetcode.cn/problems/reverse-linked-list/

## 思路：三指针迭代

`prev`（已反转部分的头）、`curr`（当前处理）、`nextNode`（暂存）。

四步顺序**不能错**：

```java
ListNode nextNode = curr.next;   // 1. 先暂存，否则改完指针就找不到后面的节点（断链）
curr.next = prev;                // 2. 反转当前节点的指向
prev = curr;                     // 3. prev 前移
curr = nextNode;                 // 4. curr 前移
return prev;                     // 结束时 curr == null，prev 是新头
```

## 复杂度

- 时间 O(n)
- 空间 O(1)（只用了三个指针）

## 关键点

1. **为什么必须第 1 步暂存**：`curr.next = prev` 一旦执行，原来 `curr → next` 的连接就断了，如果不提前保存，后面无处可去
2. **返回 `prev` 不是 `curr`**：循环结束时 `curr == null`，`prev` 才是翻转后的头
3. 边界：空链表返回 `null`、单节点返回自身，代码天然正确（循环不执行 / 执行一次）
4. 递归版也能做，但空间 O(n)；**迭代版是面试默认期待**

## 举一反三

同款"三指针反转"思想用于：92 反转链表 II（区间反转）、25 K 个一组翻转链表、234 回文链表。
