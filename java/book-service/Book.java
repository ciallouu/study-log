public class Book {
    private int id;
    private String title;
    private String author;
    private double price;

    public Book(int id, String title, String author, double price) {
        if (id <= 0) {
            throw new IllegalArgumentException("id 必须大于 0: " + id);
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title 不能为空");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("author 不能为空");
        }
        if (price < 0) {
            throw new IllegalArgumentException("price 不能为负: " + price);
        }
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', author='" + author + "', price=" + price + "}";
    }
}