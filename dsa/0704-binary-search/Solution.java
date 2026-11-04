class Solution {
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        // 闭区间模板：[left, right] 内的元素是待搜索范围
        while (left <= right) {
            // 写成 left + (right - left) / 2，避免 left + right 整数溢出
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;                 // 命中，提前返回
            } else if (nums[mid] < target) {
                left = mid + 1;             // 答案在右半边，mid 已排除
            } else {
                right = mid - 1;            // 答案在左半边，mid 已排除
            }
        }

        // 循环正常结束 = 区间为空 = 没找到
        return -1;
    }
}
