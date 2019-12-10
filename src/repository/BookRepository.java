package repository;

import database.DbConnector;
import model.Book;

import java.sql.*;
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
        String createBookTableQuery = "CREATE TABLE IF NOT EXISTS book (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(50) NOT NULL, " +
                "author VARCHAR(50) NOT NULL, " +
                "category VARCHAR(15) NOT NULL, " +
                "status VARCHAR(10) NOT NULL," +
                "publishing_date VARCHAR(4) NOT NULL," +
                "publishing_house VARCHAR(50) NOT NULL," +
                "number_of_pages INT NOT NULL)";
        try {
            PreparedStatement statement = dbConnector.getConnection().prepareStatement(createBookTableQuery);
            statement.executeUpdate();
        } catch (SQLException ex){
            System.err.println(ex.getMessage());
        }
    }

    public void addBook(Book book) throws SQLException {
        Connection connection = dbConnector.getConnection();
        String insertIntoBookQuery = "INSERT INTO Book(title, author, category, status, publishing_date, publishing_house, number_of_pages) VALUES(?, ?, ?, ?, ?, ?, ?);";

        PreparedStatement statement = connection.prepareStatement(insertIntoBookQuery, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, book.getTittle());
        statement.setString(2, book.getAuthor());
        statement.setString(3, book.getCategory());
        statement.setString(4, book.getStatus());
        statement.setString(5, book.getPublicationDate());
        statement.setString(6, book.getPublishingHouse());
        statement.setDouble(7, book.getNumberOfPages());

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

    public List<Book> getBooks()  {
        List<Book> allBooks = new ArrayList<>();
        String getBooksQuery = "Select * from book where status = 'AVAILABLE'";
        try {
            PreparedStatement statement = dbConnector.getConnection().prepareStatement(getBooksQuery);
            ResultSet resultSet = statement.executeQuery();
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
        String getBooksQuery = "Select * from book where status = 'AVAILABLE'";

        String inClause = getAllIdBooksReservedByUser(user_id);
        if (!inClause.equals("()")) {
            getBooksQuery ="Select * from book where status = 'AVAILABLE' or id in " + inClause;
        }

        PreparedStatement statement = dbConnector.getConnection().prepareStatement(getBooksQuery);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            books.add(getBookInfo(resultSet));
        }

        return books;

    }

    public List<Book> getBooksWithFilter(long user_id, String title, String author, String publishingHouse, String category) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "Select * from book where status = 'AVAILABLE' and title like ? and author like ? and publishing_house like ? and category like ?";
        String inClause = getAllIdBooksReservedByUser(user_id);
        if (!inClause.equals("()")) {
            sql = "Select * from book where (status = 'AVAILABLE' or id in " + inClause + ") and title like ? and author like ? and publishing_house like ? and category like ?";
        }

        PreparedStatement statement = dbConnector.getConnection().prepareStatement(sql);
        statement.setString(1, "%" + title + "%");
        statement.setString(2, "%" + author + "%");
        statement.setString(3, "%" + publishingHouse + "%");
        statement.setString(4, "%" + category + "%");

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            books.add(getBookInfo(resultSet));
        }

        return books;
    }

    private String getAllIdBooksReservedByUser(long user_id) throws SQLException {
        List<Long> idBooksReservedByUser = new ArrayList<>();
        String getAllIdBooksQuery = "SELECT id_book FROM rent WHERE id_user = ? AND status = 'RESERVED'";
        PreparedStatement statement = dbConnector.getConnection().prepareStatement(getAllIdBooksQuery);
        statement.setLong(1, user_id);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            idBooksReservedByUser.add(resultSet.getLong("id_book"));
        }

        String booksIdInClause = idBooksReservedByUser.stream()
                .map(n -> n.toString())
                .collect(Collectors.joining(",", "(", ")"));


        return booksIdInClause;

    }

    public int setBookStatus(Book book) throws SQLException {
        String setBookStatusQuery = "UPDATE book set status = ? where id= ?";
        PreparedStatement statement = dbConnector.getConnection().prepareStatement(setBookStatusQuery);
        statement.setString(1, book.getStatus());
        statement.setLong(2, book.getId());

        int affectedRows = statement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Updating book failed, no rows affected");
        }
        return affectedRows;
    }

    public Book findBookById(Long book_id) throws SQLException {
        String findBookByIdQuery = "SELECT * from book where id= ?";
        PreparedStatement statement = dbConnector.getConnection().prepareStatement(findBookByIdQuery);
        statement.setLong(1, book_id);
        ResultSet resultSet = statement.executeQuery();
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
