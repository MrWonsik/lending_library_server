import connector.ServerTCP;
import database.DbConnector;
import loader.BookLoader;
import loader.UserLoader;
import repository.BookRepository;
import repository.RentRepository;
import repository.UserRepository;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        DbConnector dbConnector = DbConnector.getInstance();
        dbConnector.dropAllTables();
        UserRepository.getInstance().createTable();
        BookRepository.getInstance().createTable();
        RentRepository.getInstance().createTable();
        BookLoader.initBooks();
        UserLoader.initUsers();


        ServerTCP server = new ServerTCP();
        try {
            server.runServer(5000);
            server.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dbConnector.closeConnection();
    }
}
