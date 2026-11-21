public class Solution {
    public boolean hasCycle(ListNode head) {
        ListNode fast = head;
        ListNode slow = head;

        while (fast != null && fast.next != null) {
            fast = fast.next.next;      // 先移动
            slow = slow.next;
            if (fast == slow) {         // 后比较
                return true;
            }
        }
        // 循环正常结束 = fast 走到尽头 = 无环
        return false;
    }
}
