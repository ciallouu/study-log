# 234. 回文链表

https://leetcode.cn/problems/palindrome-linked-list/

## 思路：876 + 206 拼起来（O(1) 空间版本）

```java
// ① 找中点
ListNode slow = head, fast = head;
while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }

// ② 后半段起点：偶数→slow，奇数→slow.next
ListNode cur = (fast == null) ? slow : slow.next;

// ③ 反转后半段
ListNode pre = null;
while (cur != null) { ListNode next = cur.next; cur.next = pre; pre = cur; cur = next; }

// ④ 比对：只判 pre
ListNode h = head;
while (pre != null) {
    if (h.val != pre.val) return false;
    h = h.next;
    pre = pre.next;
}
return true;
```

## 三个关键判断

### ① 后半段起点：`fast == null ? slow : slow.next`

| 节点数 | 循环结束时 | 后半段起点 |
|---|---|---|
| 偶数 `[1,2,2,1]` | `fast == null`，`slow` = 第 3 个（2） | **`slow` 本身** |
| 奇数 `[1,2,1]` | `fast != null`（停在最后），`slow` = 中点（2） | **`slow.next`** |

**中点自己不用跟自己比**——这就是奇数要 `slow.next` 的原因。

### ② 为什么 `while (pre != null)` 而不是 `while (h != null)`

反转后的后半段**总是更短或相等**（奇数时正好短 1）。
以**短的一侧**作为循环条件，长的那侧多出来的节点自然不参与比较 ——
**这就是「奇数跳过中点」的实现方式，不需要写 `if` 判断奇偶。**

### ③ 翻转后原链表被破坏

LeetCode 234 不要求复原。若要求复原（如面试追问），可在比对后再反转一次接回去。

## 踩过的坑（第一次写挂的地方）

| 坑 | 现象 | 原因 |
|---|---|---|
| `if (…) { ListNode cur = …; }` | `cannot find symbol: cur` | **作用域**：变量在大括号内声明，出了 `}` 就销毁。应在外面声明、里面赋值：`ListNode cur;` |
| `while (h.next != 0 \|\| pre.next != 0)` | `incomparable types: ListNode and int` | 节点是引用，不能跟 `0` 比；且应判 `pre != null` |
| 循环体里只有 `if/else`，没有指针前进 | **死循环** | 比对完必须 `h = h.next; pre = pre.next;` |
| `continue` 想代替「什么也不做」 | 死循环 | `continue` 会**跳过后面所有语句**，把指针前进也跳过了。空块 `{ }` 才是「什么也不做」 |

## 复杂度

- 时间 O(n)：找中点 O(n/2) + 反转 O(n/2) + 比对 O(n/2)
- 空间 **O(1)** ← 这是比「复制进数组再双指针」的 O(n) 更好的地方，面试要说出来

## 一句话总结

> **用短的一侧做循环条件，长的多出来的部分自动忽略** —— 这一招能省掉一整个奇偶分支。
