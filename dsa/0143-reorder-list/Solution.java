/**
 * 143. 重排链表
 * https://leetcode.cn/problems/reorder-list/
 *
 * L0 → L1 → … → Ln-1 → Ln
 * 变成
 * L0 → Ln → L1 → Ln-1 → L2 → Ln-2 → …
 *
 * 三块积木：876 找中点 + 206 反转 + 拉链式合并
 */
class Solution {
    public void reorderList(ListNode head) {
        // ① 找中点。注意 fast 从 head.next 起步 —— 这样 slow 停在前半段的"尾巴"
        ListNode slow = head, fast = head.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // ② 先取后半段头，再砍断（顺序不能反）
        ListNode second = slow.next;
        slow.next = null;

        // ③ 反转后半段（206 的三指针）
        ListNode cur = second;
        ListNode pre = null;
        while (cur != null) {
            ListNode temp = cur;
            cur = cur.next;
            temp.next = pre;
            pre = temp;
        }

        // ④ 拉链式交替合并：前半段一个、后半段一个
        ListNode h = head;
        while (pre != null) {              // 用短的一侧（后半段）做循环条件
            ListNode temp1 = h.next;       // 先存住两边的 next，再改指针
            ListNode temp2 = pre.next;
            h.next = pre;
            pre.next = temp1;
            h = temp1;
            pre = temp2;
        }
    }
}
