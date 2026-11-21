# 141. 环形链表

https://leetcode.cn/problems/linked-list-cycle/

## 思路：快慢指针（Floyd 判圈）

slow 每轮走 1 步，fast 每轮走 2 步。有环则必相遇，无环则 fast 先到尽头。

## 为什么 `false` 只能写在循环外

`while` 只有两种退出方式，**都意味着无环**：

| 退出条件 | 含义 |
|---|---|
| `fast == null` | fast 走到链表尽头 |
| `fast.next == null` | fast 停在最后一个节点，无法再走 2 步 |

**有环时 fast 永远不会变成 null**，所以循环只能靠 `return true` 出来。因此"循环正常结束 ⟺ 无环"，`return false` 放在循环外是逻辑必然。

## 为什么有环必定相遇

fast 相对 slow 每轮**净追 1 步**，入环后距离每轮减 1，最多一圈必追上。

### 面试追问：为什么是 2 步，不是 3 步？

相对速度 `v`、环长 `L`，保证相遇需要 `gcd(v, L) = 1`。

- fast 走 2 步 → `v = 1` → 对任何 L 都保证相遇 ✅
- fast 走 3 步 → `v = 2` → 若 L 是偶数则永远错过，死循环 ❌

## 两个易错点

1. **必须先移动、后比较**。若先 `if (fast == slow) return true;`，第一轮两者都是 `head`，任何链表都判成有环
2. **`slow` 不需要判空**。slow 永远在 fast 之后（或重合），fast 没到 null，slow 更不会

## 复杂度

- 时间 O(n)，空间 O(1)
- 对比 HashSet 记录访问过的节点：时间同为 O(n)，但空间 O(n)
