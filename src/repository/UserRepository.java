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


    private UserRepository(DbConnector dbConnector) {
        this.dbConnector = dbConnector;
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
        try {
            String createUserTableQuery = "CREATE TABLE IF NOT EXISTS User (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "first_name VARCHAR(30) NOT NULL, " +
                    "last_name VARCHAR(30) NOT NULL, " +
                    "email VARCHAR(50) NOT NULL, " +
                    "city VARCHAR(50) NOT NULL, " +
                    "street VARCHAR(100) NOT NULL, " +
                    "postal_code VARCHAR(10) NOT NULL, " +
                    "hash VARCHAR(255) NOT NULL);";
            PreparedStatement statement = dbConnector.getConnection().prepareStatement(createUserTableQuery);
            statement.executeUpdate();
        } catch (SQLException ex){
            System.err.println(ex.getMessage());
        }
    }

    public void addUser(User user) throws SQLException {
        String addUserQuery = "INSERT INTO User" +
                "(first_name, last_name, email, city, street, postal_code, hash) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = dbConnector.getConnection().prepareStatement(addUserQuery, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, user.getFirstname());
        statement.setString(2, user.getLastName());
        statement.setString(3, user.getEmail());
        statement.setString(4, user.getCity());
        statement.setString(5, user.getStreet());
        statement.setString(6, user.getPostalCode());
        statement.setString(7, user.getHash());

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
        String getUsersQuery = "Select * from user";
        try {
            PreparedStatement statement = dbConnector.getConnection().prepareStatement(getUsersQuery);
            ResultSet resultSet = statement.executeQuery();
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
        String findUserByIdQuery = "SELECT * from user where id=?";
        PreparedStatement statement = dbConnector.getConnection().prepareStatement(findUserByIdQuery);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        User user = null;
        while(resultSet.next()){
            user = getUserInfo(resultSet);
        }
        return user;
    }

    public User findUserByEmailHash(String email, String hash) throws SQLException {
        String findUserByEmailHashQuery = String.format("SELECT * from user where email = ? and hash = ?");
        PreparedStatement statement = dbConnector.getConnection().prepareStatement(findUserByEmailHashQuery);
        statement.setString(1, email);
        statement.setString(2, hash);
        ResultSet resultSet = statement.executeQuery();

        User user = null;
        while(resultSet.next()){
            user = getUserInfo(resultSet);
        }
        return user;
    }

    public User findUserByEmail(String email) throws SQLException {
        String findUserByEmailQuery = "SELECT * from user where email = ?";
        PreparedStatement statement = dbConnector.getConnection().prepareStatement(findUserByEmailQuery);
        statement.setString(1, email);
        ResultSet resultSet = statement.executeQuery();
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

