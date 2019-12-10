package database;

import java.sql.*;

public class DbConnector {
    private String driver = "com.mysql.jdbc.Driver";
    private String url = "jdbc:mysql://localhost:3306/lending_library?user=root&password=";
    private Connection connection;
    private volatile static DbConnector dbConnector;

    private DbConnector(){
        if(loadDriver(this.driver)){
            try {
                this.connection = DriverManager.getConnection(url);
            } catch (SQLException e){
                System.err.println("Blad przy tworzeniu polaczenia");
            }
        }
    }

    public static DbConnector getInstance(){
        if (dbConnector == null) {
            synchronized (DbConnector.class){
                if(dbConnector == null){
                    dbConnector = new DbConnector();
                }
            }
        }

        return dbConnector;
    }

    public Connection getConnection() {
        return connection;
    }

    private boolean loadDriver(String driver){
        try{
            Class.forName(driver).newInstance();
            return true;
        } catch (Exception e){
            System.err.println("Błąd przy ładowaniu sterownika bazy!");
            return false;
        }
    }

    public Statement createStatement(){
        try {
            return this.connection.createStatement();
        } catch (SQLException e){
            System.err.println(e);
        }
        return null;
    }

    public void closeConnection(){
        try{
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(4);
        }
    }


    public void dropAllTables(){
        Statement statement = createStatement();
        try {
            statement.executeUpdate("DROP TABLE IF EXISTS rent");
            statement.executeUpdate("DROP TABLE IF EXISTS user");
            statement.executeUpdate("DROP TABLE IF EXISTS book");
        } catch (SQLException ex){
            System.err.println(ex.getMessage());
        }
    }
}
