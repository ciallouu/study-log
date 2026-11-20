class Solution {
    public int minSubArrayLen(int target, int[] nums) {
        int left = 0;
        int n = nums.length;
        int sum = 0;
        int ans = Integer.MAX_VALUE;          // 哨兵：先设成最大，方便取 min

        for (int right = 0; right < n; right++) {
            sum += nums[right];               // 右指针扩张

            while (sum >= target) {           // 达标就收缩左指针
                ans = Math.min(ans, right - left + 1);  // 窗口长度 = right-left+1
                sum -= nums[left];            // 先移出最左元素
                left++;                       // 左指针右移
            }
        }

        return ans == Integer.MAX_VALUE ? 0 : ans;   // 从没达标过就返回 0
    }
}
