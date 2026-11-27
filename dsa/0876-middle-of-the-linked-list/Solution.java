/**
 * 876. 链表的中间结点
 * https://leetcode.cn/problems/middle-of-the-linked-list/
 *
 * 快慢指针：fast 一次两步、slow 一次一步。
 * 偶数个节点时返回「后中点」（LeetCode 要求），正好是 slow 停的位置。
 */
class Solution {
    public ListNode middleNode(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        // 两个条件都要；顺序不能反：fast == null 时若先算 fast.next 会空指针
        while (fast != null && fast.next != null) {
            slow = slow.next;       // 一步
            fast = fast.next.next;  // 两步
        }
        return slow;
    }
}
