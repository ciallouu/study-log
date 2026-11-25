class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode slow = dummy;
        ListNode fast = dummy;

        // 先让 fast 走 n + 1 步（不是 n 步）
        for (int i = 0; i < n + 1; i++) fast = fast.next;

        // fast 与 slow 同步前进，直到 fast 到达 null
        while (fast != null) {
            fast = fast.next;
            slow = slow.next;
        }

        slow.next = slow.next.next;   // slow 停在待删节点的前驱
        return dummy.next;
    }
}
