package repository;

import database.DbConnector;
import model.Book;
import model.Rent;
import model.User;

import java.sql.*;

public class RentRepository {

    private volatile static RentRepository rentRepository;

    private DbConnector dbConnector;
    private Statement statement;

    private RentRepository(DbConnector dbConnector) {
        this.dbConnector = dbConnector;
        this.statement = dbConnector.createStatement();
    }

    public static RentRepository getInstance() {
        if (rentRepository == null) {
            synchronized (RentRepository.class) {
                if (rentRepository == null) {
                    rentRepository = new RentRepository(DbConnector.getInstance());
                }
            }
        }

        return rentRepository;
    }

    public void createTable() {
        dbConnector.executeUpdate(this.statement,
                "CREATE TABLE IF NOT EXISTS rent (" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "id_user INT NOT NULL, " +
                        "id_book INT NOT NULL, " +
                        "date_of_action DATE NOT NULL default CURRENT_TIMESTAMP, " +
                        "status VARCHAR(10) NOT NULL, " +
                        "FOREIGN KEY (id_user) REFERENCES user(id) ON DELETE RESTRICT," +
                        "FOREIGN KEY (id_book) REFERENCES book(id) ON DELETE RESTRICT);");
    }

    public void addRent(Rent rent) throws SQLException {
        Connection connection = dbConnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                String.format("INSERT INTO rent" +
                                "(id_user, id_book, status) " +
                                "VALUES('%s', '%s', '%s');",
                        rent.getIdUser(),
                        rent.getIdBook(),
                        rent.getStatus()),
                Statement.RETURN_GENERATED_KEYS);


        int affectedRows = statement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Added rent failed, no rows affected");
        }

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                rent.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("Createing user failed, no ID obtained.");
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public void cancelRent(Rent rent) throws SQLException {
        int affectedRows = dbConnector.executeUpdate(statement, String.format("UPDATE rent SET status = '" +
                rent.getStatus() + "' where id = " + rent.getId()));

        if (affectedRows == 0) {
            throw new SQLException("Canceled rent failed, no rows affected");
        }
    }

    public Rent findRentByUserAndBookAndStatus(User user, Book book, String status) throws SQLException {
        ResultSet resultSet = dbConnector.executeQuery(this.statement,
                String.format(
                        "SELECT * from rent where id_user=%d and id_book =%d and status = '%s';",
                        user.getId(),
                        book.getId(),
                        status)
        );
        Rent rent = null;
        while (resultSet.next()) {
            rent = new Rent(resultSet.getLong("id"), resultSet.getLong("id_user"), resultSet.getLong("id_book"), resultSet.getString("status"));
        }
        return rent;
    }
}
