class Solution {
    public int[] sortedSquares(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        int left = 0;
        int right = n - 1;
        int pos = n - 1;

        while (left <= right) {
            int l = nums[left] * nums[left];
            int r = nums[right] * nums[right];
            if (l > r) {
                res[pos--] = l;
                left++;
            } else {
                res[pos--] = r;
                right--;
            }
        }
        return res;
    }
}
