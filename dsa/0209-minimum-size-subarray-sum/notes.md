# 209. 长度最小的子数组

https://leetcode.cn/problems/minimum-size-subarray-sum/

## 思路：滑动窗口（右扩左缩）

- `right` 负责扩张：`sum += nums[right]`
- 当 `sum >= target` 时，`left` 负责收缩，并持续收缩直到 `sum < target`

前提：**所有元素都是正数**（题目 `1 <= nums[i]`），窗口和才单调，双指针才能不回退地扫一遍 O(n)。

## 复杂度

- 时间 O(n)：每个元素被 right 加一次、被 left 减一次，共 O(2n)
- 空间 O(1)

## 踩的坑 / 面试点

1. **窗口长度 = `right - left + 1`**，忘写 `+1` 会整体少 1（最高频 off-by-one）
2. 内层必须用 `while` 不是 `if`：收缩一次后可能仍 `>= target`，要连续收缩
3. 收缩循环里顺序：先 `Math.min` 记录（此刻窗口合法）→ 再 `sum -= nums[left]` → 再 `left++`
4. 无解用哨兵 `Integer.MAX_VALUE`，最后 `ans == MAX_VALUE` 则返回 0（官方样例 3 就考这个）
5. 若允许负数，滑动窗口失效（left 右移可能让和变大），要用前缀和 + 二分

## 双指针形态

滑动窗口（右扩左缩，一个 for 一个 while）——第三种双指针形态。
