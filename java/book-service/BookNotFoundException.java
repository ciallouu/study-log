public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String message) {
        super(message);      // 构造器不会被继承，必须显式声明并交给父类保存
    }
}