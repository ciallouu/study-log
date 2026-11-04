# 704. 二分查找

https://leetcode.cn/problems/binary-search/

## 思路

维护一个**闭区间** `[left, right]`，每次用 `mid` 把区间砍掉一半。

不变式：如果 `target` 存在，它一定在 `[left, right]` 内。

## 模板配套（关键）

| | 闭区间（本题用的） | 左闭右开 |
|---|---|---|
| 初始化 | `right = nums.length - 1` | `right = nums.length` |
| 循环条件 | `while (left <= right)` | `while (left < right)` |
| 收缩 | `left = mid + 1` / `right = mid - 1` | `left = mid + 1` / `right = mid` |

**两种模板绝不能混用。** 闭区间里 `mid` 已经检查过了，所以可以 ±1 排除掉。

## 复杂度

- 时间 O(log n)
- 空间 O(1)

## 踩的坑

1. `(left + right) / 2` 在两者都很大时会整数溢出成负数 → 必须写 `left + (right - left) / 2`
2. `-1` 要写在 `while` 循环**之后**，因为循环可能一次都不执行（空数组）
3. `mid` 只在循环开头算一次，不要在每个分支里各算一遍（漏一次就死循环）
4. Java 数组长度是 `nums.length` 字段，不是 Python 的 `len(nums)`，也不是 C++ 的 `size()`
