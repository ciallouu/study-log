class Solution {
    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;

        while (curr != null) {
            ListNode nextNode = curr.next; // 1. 暂存下一个节点，防止断链
            curr.next = prev;              // 2. 反转当前节点指针
            prev = curr;                   // 3. prev 向前推进
            curr = nextNode;               // 4. curr 向前推进
        }
        return prev;                       // curr 为空时，prev 指向新链表的头节点
    }
}
