package controller;

import model.User;
import repository.UserRepository;

import java.sql.SQLException;

public class UserController {

    public static String getUserInfo(String email) {
        User user = null;
        try {
            user = UserRepository.getInstance().findUserByEmail(email);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(user == null) {
            return "undefined;undefined;0";
        }

        return user.getFirstname() + ";" + user.getLastName() + ";" + user.getId();
    }
}
