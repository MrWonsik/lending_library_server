package repository;

import database.DbConnector;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserRepository {

    private volatile static UserRepository userRepository;

    private DbConnector dbConnector;
    private Statement statement;


    private UserRepository(DbConnector dbConnector) {
        this.dbConnector = dbConnector;
        this.statement = dbConnector.createStatement();
    }

    public static UserRepository getInstance(){
        if(userRepository == null){
            synchronized (UserRepository.class){
                if(userRepository == null){
                    userRepository = new UserRepository(DbConnector.getInstance());
                }
            }
        }

        return userRepository;
    }

    public void createTable(){
        dbConnector.executeUpdate(this.statement,
                "CREATE TABLE IF NOT EXISTS User (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "first_name VARCHAR(30) NOT NULL, " +
                        "last_name VARCHAR(30) NOT NULL, " +
                        "email VARCHAR(50) NOT NULL, " +
                        "city VARCHAR(50) NOT NULL, " +
                        "street VARCHAR(100) NOT NULL, " +
                        "postal_code VARCHAR(10) NOT NULL, " +
                        "hash VARCHAR(255) NOT NULL);");
    }

    public void addUser(User user) throws SQLException {
        Connection connection = dbConnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                String.format("INSERT INTO User" +
                        "(first_name, last_name, email, city, street, postal_code, hash) " +
                        "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                        user.getFirstname(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getCity(),
                        user.getStreet(),
                        user.getPostalCode(),
                        user.getHash()),
                        Statement.RETURN_GENERATED_KEYS);

        int affectedRows = statement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating user failed, no rows affected");
        }
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("Createing user failed, no ID obtained.");
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public List<User> getUsers() {
        List<User> allUsers = new ArrayList<>();
        ResultSet resultSet = dbConnector.executeQuery(this.statement, "Select * from user");
        try {
            while (resultSet.next()) {
                allUsers.add(getUserInfo(resultSet));
            }
            return allUsers;
        } catch (SQLException e) {
            System.err.println(e);
        }

        return Collections.emptyList();
    }

    public User findUserById(Long id) throws SQLException {
        ResultSet resultSet = dbConnector.executeQuery(this.statement, String.format("SELECT * from user where id=%d", id));
        User user = null;
        while(resultSet.next()){
            user = getUserInfo(resultSet);
        }
        return user;
    }

    public User findUserByEmailHash(String email, String hash) throws SQLException {
        ResultSet resultSet = dbConnector.executeQuery(this.statement, String.format("SELECT * from user where email = '%s' and hash = '%s';", email, hash));
        User user = null;
        while(resultSet.next()){
            user = getUserInfo(resultSet);
        }
        return user;
    }

    private User getUserInfo(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("email"),
                resultSet.getString("city"),
                resultSet.getString("street"),
                resultSet.getString("postal_code"));
    }
}

