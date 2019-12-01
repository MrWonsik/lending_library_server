package repository;

import database.DbConnector;
import model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookRepository {

    private volatile static BookRepository bookRepository;

    private DbConnector dbConnector;
    private Statement statement;

    private BookRepository(DbConnector dbConnector) {
        this.dbConnector = dbConnector;
        this.statement = dbConnector.createStatement();
    }

    public static BookRepository getInstance() {
        if(bookRepository == null) {
            synchronized (BookRepository.class){
                if(bookRepository == null){
                    bookRepository = new BookRepository(DbConnector.getInstance());
                }
            }
        }

        return bookRepository;
    }

    public void createTable(){
        dbConnector.executeUpdate(this.statement,
                "CREATE TABLE IF NOT EXISTS book (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "title VARCHAR(50) NOT NULL, " +
                        "author VARCHAR(50) NOT NULL, " +
                        "category VARCHAR(15) NOT NULL, " +
                        "status VARCHAR(10) NOT NULL);");
    }

    public void addBook(Book book) throws SQLException {
        Connection connection = dbConnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                String.format("INSERT INTO Book" +
                                "(title, author, category, status) " +
                                "VALUES('%s', '%s', '%s', '%s');",
                        book.getTittle(),
                        book.getAuthor(),
                        book.getCategory(),
                        book.getStatus()),
                Statement.RETURN_GENERATED_KEYS);

        int affectedRows = statement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Added book failed, no rows affected");
        }
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                book.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("Added book failed, no ID obtained.");
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public List<Book> getBooks() {
        List<Book> allBooks = new ArrayList<>();
        ResultSet resultSet = dbConnector.executeQuery(this.statement, "Select * from book");
        try {
            while (resultSet.next()) {
                allBooks.add(getBookInfo(resultSet));
            }

            return allBooks;
        } catch (SQLException e) {
            System.err.println(e);
        }

        return Collections.emptyList();
    }

    public int setBookStatus(Book book) throws SQLException {
        int affectedRows = dbConnector.executeUpdate(this.statement, "UPDATE book set status = '" + book.getStatus() + "' where id=" + book.getId());
        if (affectedRows == 0) {
            throw new SQLException("Updateting book failed, no rows affected");
        }
        return affectedRows;
    }

    public Book findBookById(Long book_id) throws SQLException {
        ResultSet resultSet = dbConnector.executeQuery(this.statement, "SELECT * from book where id=" + book_id);
        Book book = null;
        while(resultSet.next()){
            book = getBookInfo(resultSet);
        }

        return book;
    }

    private Book getBookInfo(ResultSet resultSet) throws SQLException {
        return new Book(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getString("author"),
                resultSet.getString("category"),
                resultSet.getString("status"));
    }
}
