# 24. 两两交换链表中的节点

https://leetcode.cn/problems/swap-nodes-in-pairs/

## 思路：哑结点 + 三指针重排

处理 `cur → A → B → C`，目标是 `cur → B → A → C`：

```java
ListNode temp  = cur.next;              // A
ListNode temp1 = cur.next.next.next;    // C  ← 必须在动指针之前存
cur.next       = cur.next.next;         // cur → B
cur.next.next  = temp;                  // B → A
temp.next      = temp1;                 // A → C
cur = cur.next.next;                    // 跳到下一对的"前驱"
```

## 复杂度

- 时间 O(n)
- 空间 O(1)

## 关键点

1. **`temp1` 必须先取**：`cur.next = cur.next.next` 一旦执行，cur 就不再指向 A，后续关系会变，提前存最稳
2. **循环条件两个都要有**：`cur.next != null`（后面还有节点）+ `cur.next.next != null`（后面有两个节点才够换）。**这正是奇数个节点的处理方式**——最后一轮不执行，末尾单节点原样保留
3. **`cur = cur.next.next` 必须跳两格**：交换后 cur 停在 A 上，下一对从 A 之后开始
4. 哑结点的作用：首对交换会改 head，有 dummy 就不需要特判
5. 常见错误：把 `cur = cur.next` 写成一格 → 死循环或重复交换

## 举一反三

25 K 个一组翻转链表是这题的推广（每 k 个一组）；206 反转链表是它的简化版（无分组）。
