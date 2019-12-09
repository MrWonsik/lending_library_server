package repository;

import database.DbConnector;
import model.Book;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BookRepository {

    private volatile static BookRepository bookRepository;

    private DbConnector dbConnector;

    private BookRepository(DbConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    public static BookRepository getInstance() {
        if (bookRepository == null) {
            synchronized (BookRepository.class) {
                if (bookRepository == null) {
                    bookRepository = new BookRepository(DbConnector.getInstance());
                }
            }
        }

        return bookRepository;
    }

    public void createTable() {
        dbConnector.executeUpdate(dbConnector.createStatement(),
                "CREATE TABLE IF NOT EXISTS book (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "title VARCHAR(50) NOT NULL, " +
                        "author VARCHAR(50) NOT NULL, " +
                        "category VARCHAR(15) NOT NULL, " +
                        "status VARCHAR(10) NOT NULL," +
                        "publishing_date VARCHAR(4) NOT NULL," +
                        "publishing_house VARCHAR(50) NOT NULL," +
                        "number_of_pages INT NOT NULL);");
    }

    public void addBook(Book book) throws SQLException {
        Connection connection = dbConnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                String.format("INSERT INTO Book" +
                                "(title, author, category, status, publishing_date, publishing_house, number_of_pages) " +
                                "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%d');",
                        book.getTittle(),
                        book.getAuthor(),
                        book.getCategory(),
                        book.getStatus(),
                        book.getPublicationDate(),
                        book.getPublishingHouse(),
                        book.getNumberOfPages()),
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
        ResultSet resultSet = dbConnector.executeQuery(dbConnector.createStatement(), "Select * from book where status = 'AVAILABLE'");
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

    public List<Book> getBooks(long user_id) throws SQLException {
        List<Book> books = new ArrayList<>();
        ResultSet resultSet = dbConnector.executeQuery(dbConnector.createStatement(), "Select * from book where status = 'AVAILABLE'");

        String inClause = getAllIdBooksReservedByUser(user_id);
        if (!inClause.equals("()")) {
            resultSet = dbConnector.executeQuery(dbConnector.createStatement(), "Select * from book where status = 'AVAILABLE' or id in " + inClause);
        }

        while (resultSet.next()) {
            books.add(getBookInfo(resultSet));
        }

        return books;

    }

    public List<Book> getBooksWithFilter(long user_id, String title, String author, String publishingHome, String category) throws SQLException {
        List<Book> books = new ArrayList<>();
        ResultSet resultSet = dbConnector.executeQuery(dbConnector.createStatement(), MessageFormat.format("Select * from book where status = ''AVAILABLE'' and title like ''%{0}%'' and author like ''%{1}%'' and publishing_house like ''%{2}%'' and category like ''%{3}%''", title, author, publishingHome, category));

        String inClause = getAllIdBooksReservedByUser(user_id);
        if (!inClause.equals("()")) {
            resultSet = dbConnector.executeQuery(dbConnector.createStatement(), MessageFormat.format("Select * from book where (status = ''AVAILABLE'' or id in {0}) and title like ''%{1}%'' and author like ''%{2}%'' and publishing_house like ''%{3}%'' and category like ''%{4}%''", inClause, title, author, publishingHome, category));
        }

        while (resultSet.next()) {
            books.add(getBookInfo(resultSet));
        }

        return books;
    }

    private String getAllIdBooksReservedByUser(long user_id) throws SQLException {
        List<Long> idBooksReservedByUser = new ArrayList<>();
        ResultSet idBooksForUserId = dbConnector.executeQuery(dbConnector.createStatement(),
                "SELECT id_book FROM rent WHERE id_user = " + user_id + " AND status = 'RESERVED'");

        while (idBooksForUserId.next()) {
            idBooksReservedByUser.add(idBooksForUserId.getLong("id_book"));
        }

        String booksIdInClause = idBooksReservedByUser.stream()
                .map(n -> n.toString())
                .collect(Collectors.joining(",", "(", ")"));


        return booksIdInClause;

    }

    public int setBookStatus(Book book) throws SQLException {
        int affectedRows = dbConnector.executeUpdate(dbConnector.createStatement(), "UPDATE book set status = '" + book.getStatus() + "' where id=" + book.getId());
        if (affectedRows == 0) {
            throw new SQLException("Updating book failed, no rows affected");
        }
        return affectedRows;
    }

    public Book findBookById(Long book_id) throws SQLException {
        ResultSet resultSet = dbConnector.executeQuery(dbConnector.createStatement(), "SELECT * from book where id=" + book_id);
        Book book = null;
        while (resultSet.next()) {
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
                resultSet.getString("status"),
                resultSet.getString("publishing_date"),
                resultSet.getString("publishing_house"),
                resultSet.getInt("number_of_pages"));
    }


}
