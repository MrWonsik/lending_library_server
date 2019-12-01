package controller;

import model.User;
import repository.UserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class UserAuthorization {

    public static String authoriseUser(String hash, String email) {
        try {
            User user = UserRepository.getInstance().findUserByEmailHash(email, hash);

            if (user != null) {
                return "correct";
            }

            return "incorrect";
        } catch (SQLException ex) {
            System.err.println(ex);
            return "smth wrong";
        }

    }

    public static String generateHash(String password) {
        String passwordToHash = password;
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
