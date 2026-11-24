public class Test {
    public static void main(String[] args) {
        MyLinkedList list = new MyLinkedList();
        System.out.println(list);          // []
        list.addFirst(1);
        list.addLast(2);
        System.out.println(list);          // [1, 2]
        list.reverse();
        list.addLast(9);
        System.out.println(list);          // [3, 2, 9]  ← 这条能过，reverse 就算真写对了
    }
}