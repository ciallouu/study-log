class Solution {
    public ListNode swapPairs(ListNode head) {
        ListNode dummyHead = new ListNode(-1, head);   // 哑结点，省掉头节点特判
        ListNode cur = dummyHead;

        while (cur.next != null && cur.next.next != null) {
            ListNode temp = cur.next;                  // A
            ListNode temp1 = cur.next.next.next;       // C（必须在动指针前存好）
            cur.next = cur.next.next;                  // cur -> B
            cur.next.next = temp;                      // B -> A
            temp.next = temp1;                         // A -> C
            cur = cur.next.next;                       // 前进两格
        }
        return dummyHead.next;
    }
}
