class Solution {
    public ListNode removeElements(ListNode head, int val) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;          // dummy 是钉子，从这里起永不移动
        ListNode prev = dummy;      // prev 是游标，只有它动

        while (prev.next != null) {
            if (prev.next.val == val) {
                prev.next = prev.next.next;   // 删掉它，prev 原地不动
            } else {
                prev = prev.next;              // 只有「不删」时才前进
            }
        }
        return dummy.next;          // 锚点带我们回到真正的头
    }
}
