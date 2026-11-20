import java.util.List;

public interface BookService {
//    @throws IllegalArgumentException id
    void add(Book book);

    Book findById(int id);

    List<Book> listAll();
    Book remove(int id);
}
