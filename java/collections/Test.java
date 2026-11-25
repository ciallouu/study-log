public class Test {
    public static void main(String[] args) {
        MyLinkedList list = new MyLinkedList();

        // ① 空表
        System.out.println("空表        : " + list + "  size=" + list.size() + "  isEmpty=" + list.isEmpty());

        // ② 加头 + 加尾
        list.addFirst(1);
        list.addLast(2);
        System.out.println("addFirst+Last: " + list + "  size=" + list.size());

        // ③ 反转（关键：head/tail 有没有交换）
        list.reverse();
        System.out.println("反转        : " + list);
        list.addLast(9);
        System.out.println("反转后加尾  : " + list + "   <- 若不是 [2, 1, 9] 说明 head/tail 没换");

        // ④ 删头
        list.addFirst(0);
        list.addLast(3);
        System.out.println("加两头      : " + list);
        list.remove(0);
        System.out.println("删头        : " + list);

        // ⑤ 删尾 —— 测 tail 是否回退到 prev
        int last = list.remove(list.size() - 1);
        System.out.println("删尾(值=" + last + ") : " + list);
        list.addLast(7);
        System.out.println("删尾后加尾  : " + list + "   <- 7 若丢失说明 tail 没回退");

        // ⑥ 删中间
        mid(list);
        System.out.println("删中间      : " + list);

        // ⑦ get 取值
        System.out.println("get(0)=" + list.get(0) + "  get(last)=" + list.get(list.size() - 1));

        // ⑧ 越界必须抛异常
        try {
            list.get(list.size());
            System.out.println("越界检查    : ❌ 没有抛异常");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("越界检查    : ✅ " + e.getMessage());
        }

        // ⑨ 全部删空后必须是 []
        while (!list.isEmpty()) {
            list.remove(0);
        }
        System.out.println("全删空      : " + list + "  size=" + list.size() + "  isEmpty=" + list.isEmpty());
        list.addFirst(5);
        System.out.println("空表再加头  : " + list);
    }

    private static void mid(MyLinkedList l) {
        if (l.size() >= 3) {
            l.remove(l.size() / 2);
        }
    }
}
