/**
 * 234. 回文链表
 * https://leetcode.cn/problems/palindrome-linked-list/
 *
 * 三步拼接：876 找中点 → 206 反转后半段 → 双指针逐节点比对。
 * 时间 O(n)，空间 O(1)（比「复制到数组」的 O(n) 更优，面试常考这个版本）。
 */
class Solution {
    public boolean isPalindrome(ListNode head) {
        // 边界：空链表 / 单节点
        if (head == null || head.next == null) return true;

        // ① 找中点（876）：结束后 slow 中点，fast 为 null 说明偶数个
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // ② 后半段起点：偶数是 slow 本身，奇数是 slow.next（中点自己不用比）
        ListNode cur = (fast == null) ? slow : slow.next;

        // ③ 反转后半段（206 三指针）
        ListNode pre = null;
        while (cur != null) {
            ListNode next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }

        // ④ 比对：只判 pre——反转后的后半段总是「短或相等」，走完就停，
        //    奇数长度时多出来的那个中间节点自然不参与比较
        ListNode h = head;
        while (pre != null) {
            if (h.val != pre.val) return false;
            h = h.next;
            pre = pre.next;
        }
        return true;
    }
}
