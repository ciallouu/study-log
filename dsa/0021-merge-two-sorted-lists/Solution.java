class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(-1);   // 哑结点：省掉大量头节点判空
        ListNode tail = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                tail.next = list1;
                list1 = list1.next;
            } else {
                tail.next = list2;
                list2 = list2.next;
            }
            tail = tail.next;
        }

        // 拼接剩余未遍历完的部分（另一条已经空了）
        tail.next = (list1 != null) ? list1 : list2;

        return dummy.next;
    }
}
