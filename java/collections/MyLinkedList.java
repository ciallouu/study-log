public class MyLinkedList {

    private Node head;
    private Node tail;
    private int size;

    private static class Node {
        int val;
        Node next;
        Node(int val) { this.val = val; }
    }
    public void addFirst(int v) {
        Node n = new Node(v);
        n.next = head;
        head = n;
        size++;
        if (size == 1) {
            tail = n;
        }
    }
    //插入尾
    public void addLast(int v) {
        Node n = new Node(v);
        if (size == 0) {          // ① 先判断空表
            head = n;
            tail = n;
            size++;
            return;
        }
        tail.next = n;
        tail = n;
        size++;
    }

    public int get(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException("下标越界: " + i + ", size=" + size);
        }
        Node cur = head;                 // ② 从 head 开始
        for (int k = 0; k < i; k++) {    // ③ 走 i 步（不是 size 步）
            cur = cur.next;
        }
        return cur.val;
    }

    public int remove(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException("下标越界: " + i + ", size=" + size);
        }
        int removed;
        if (i == 0) {
            removed = head.val;
            head = head.next;
            if (size == 1) {             // ④ 删完变空，tail 必须清掉
                tail = null;
            }
        } else {
            Node prev = head;
            for (int k = 0; k < i - 1; k++) {
                prev = prev.next;
            }
            removed = prev.next.val;
            prev.next = prev.next.next;
            if (prev.next == null) {     // ⑤ 删的是尾，tail 回退到 prev
                tail = prev;
            }
        }
        size--;
        return removed;
    }

    public void reverse() {
        Node cur = head;
        Node pre = null;
        while (cur != null) {
            Node next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        Node oldHead = head;
        head = tail;
        tail = oldHead;
    }

    @Override
    public String toString() {
        if (size == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        Node cur = head;
        while (cur != null) {
            sb.append(cur.val);
            if (cur.next != null) sb.append(", ");
            cur = cur.next;
        }
        return sb.append("]").toString();
    }

    // 字段私有、方法暴露：外界只能问"有几个"，不能改 size
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
