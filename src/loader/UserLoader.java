package loader;

import controller.UserAuthorization;
import model.User;
import repository.UserRepository;

import java.sql.SQLException;
import java.util.List;

public class UserLoader {

    public static void initUsers(){
        try {
            new User("Tomek", "Tomalski", "tomek@gmail.com", "Rzeszow", "Kwiatowa", "23-342", UserAuthorization.generateHash("Tomek1"));
            new User("Bromek", "Bromalski", "bromek@gmail.com", "Warszawa", "Rumiankowa", "15-152", UserAuthorization.generateHash("Bromek1") );
            new User("Atomek", "Atomalski", "atomek@gmail.com", "Krakow", "Publiczna", "25-332", UserAuthorization.generateHash("Atomek1") );
        } catch (SQLException e) {
            System.err.println(e);
        }

        List<User> users = UserRepository.getInstance().getUsers();

        System.out.println(users);
    }
}
