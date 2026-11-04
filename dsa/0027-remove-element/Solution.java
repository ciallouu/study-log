class Solution {
    // 解法一：快慢指针（推荐，能保持相对顺序）
    public int removeElement(int[] nums, int val) {
        int slow = 0;   // slow = 下一个该写入的位置，也等于已保留元素个数

        for (int fast = 0; fast < nums.length; fast++) {
            if (nums[fast] != val) {
                nums[slow] = nums[fast];
                slow++;
            }
        }

        // 循环结束后 slow 就是新数组长度 k
        return slow;
    }

    // 解法二：相向双指针（赋值次数最少，但会打乱顺序——本题允许）
    public int removeElement2(int[] nums, int val) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            if (nums[left] == val) {
                nums[left] = nums[right];   // 把末尾元素搬过来填坑
                right--;
                // 注意：这里不能 left++，搬过来的新值还没检查过
            } else {
                left++;
            }
        }

        return left;
    }
}
