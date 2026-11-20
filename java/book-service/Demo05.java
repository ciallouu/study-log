public class Demo05 {
    public static void main(String[] args) {
        BookService service = new MemoryBookService();   // 只认接口

        service.add(new Book(1, "Java编程思想", "Bruce Eckel", 99.0));
        service.add(new Book(2, "深入理解Java虚拟机", "周志明", 79.0));

        System.out.println(service.findById(1));
        System.out.println("全部: " + service.listAll());

        Book removed = service.remove(1);
        System.out.println("删除了: " + removed);
        System.out.println("删除后: " + service.listAll());

        try {
            service.remove(999);        // 触发自定义异常
        } catch (BookNotFoundException e) {
            System.out.println("捕获异常: " + e.getMessage());
        }
    }
}