package controller;

import model.User;
import repository.UserRepository;

import java.sql.SQLException;

public class UserController {

    public static User getUserInfo(String email) {
        User user = null;
        try {
            user = UserRepository.getInstance().findUserByEmail(email);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
}
