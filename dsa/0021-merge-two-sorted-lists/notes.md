# 21. 合并两个有序链表

https://leetcode.cn/problems/merge-two-sorted-lists/

## 思路：哑结点 + 尾指针

建一个虚拟头节点 `dummy`，用 `tail` 不断把两条链上较小的节点接到尾巴上。

```java
ListNode dummy = new ListNode(-1);
ListNode tail = dummy;

while (list1 != null && list2 != null) {
    if (list1.val <= list2.val) { tail.next = list1; list1 = list1.next; }
    else                        { tail.next = list2; list2 = list2.next; }
    tail = tail.next;
}
tail.next = (list1 != null) ? list1 : list2;   // 接剩余部分
return dummy.next;
```

## 复杂度

- 时间 O(m + n)
- 空间 O(1)（原地拼接，只用了两个指针）

## 关键点

1. **哑结点（dummy node）是链表题的核心技巧**：有了它，"结果链表的头是哪个"这个问题就不存在了，省掉一堆 `if (head == null)` 的边界判断，最后 `return dummy.next` 即可
2. **循环条件是 `&&` 而不是 `||`**：只要有一条走完就停，剩下那条直接整段接上
3. **`tail.next = 剩下的那条` 必须写**：忘了这一步会丢掉后半段，这是本题最常见错误
4. 用 `<=` 而非 `<` 也不影响正确性（相等时取哪条都行），但保证稳定合并可用 `<=`

## 举一反三

哑结点套路适用于：23 合并 K 个升序链表、86 分隔链表、203 移除链表元素（头节点也可能被删）、2 两数相加。
