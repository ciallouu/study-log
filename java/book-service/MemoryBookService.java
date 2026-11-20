import java.util.ArrayList;
import java.util.List;

public class MemoryBookService implements BookService {

    private final List<Book> books = new ArrayList<>();   // 注意是 List 不是 ArrayList

    @Override
    public void add(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("book 不能为 null");
        }
        if (findById(book.getId()) != null) {          // 复用已有方法
            throw new IllegalArgumentException("id 重复: " + book.getId());
        }
        books.add(book);
    }

    @Override
    public Book findById(int id) {
        // 遍历，找到就 return；找不到 return null
        // 提示：id 用 int 比较，不要用 == 比 Integer
        for(Book b:books){
            if(b.getId() == id){
                return b;
            }
        }
        return null;
    }

    @Override
    public List<Book> listAll() {
        // 返回副本，别把内部的 list 直接给出去
        return new ArrayList<>(books);
    }

    @Override
    public Book remove(int id) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == id) {
                return books.remove(i);      // List.remove(int) 返回被删元素
            }
        }
        throw new BookNotFoundException("图书不存在: id=" + id);
    }
}