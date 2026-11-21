public class Solution {
    public ListNode detectCycle(ListNode head) {
        ListNode fast = head;
        ListNode slow = head;

        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;

            if (fast == slow) {
                ListNode p1 = head;   // 一个从【头】出发
                ListNode p2 = slow;   // 一个从【相遇点】出发
                while (p1 != p2) {
                    p1 = p1.next;
                    p2 = p2.next;
                }
                return p1;            // 相遇处即环入口
            }
        }
        return null;
    }
}
