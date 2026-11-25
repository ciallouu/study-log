class MyLinkedList {

    private class Node {
        int val;
        Node next;
        Node(int val) { this.val = val; }
    }

    private Node dummy = new Node(0);   // 哨兵：dummy.next 才是真正的头结点
    private int size = 0;               // 只有两个字段，没有 head / tail

    public MyLinkedList() { }

    public int get(int index) {
        if (index < 0 || index >= size) return -1;   // 题目约定：越界返回 -1，不抛异常
        Node cur = dummy.next;
        for (int i = 0; i < index; i++) cur = cur.next;
        return cur.val;
    }

    public void addAtHead(int val) { addAtIndex(0, val); }

    public void addAtTail(int val) { addAtIndex(size, val); }

    public void addAtIndex(int index, int val) {
        if (index > size) return;      // 越界：什么都不做
        if (index < 0) index = 0;      // 负数：当头部插入
        Node prev = dummy;
        for (int i = 0; i < index; i++) prev = prev.next;
        Node node = new Node(val);
        node.next = prev.next;
        prev.next = node;
        size++;
    }

    public void deleteAtIndex(int index) {
        if (index < 0 || index >= size) return;
        Node prev = dummy;
        for (int i = 0; i < index; i++) prev = prev.next;
        prev.next = prev.next.next;    // 删头/删中/删尾都是这一句
        size--;
    }
}
