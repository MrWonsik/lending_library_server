package controller;

import exception.BookNotFoundException;
import exception.RentNotFoundException;
import exception.UserNotFoundException;
import model.Book;
import model.Rent;
import model.User;
import repository.BookRepository;
import repository.RentRepository;
import repository.UserRepository;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;


public class BookReservation {

    public static String reserveBook(Long user_id, Long book_id) {
        synchronized (book_id) {
            try {

                Book book = checkIfBookExists(book_id);
                User user = checkIfUserExists(user_id);
                if (!isBookAlreadyReserved(book)) {
                    finalizeReserveBook(book, user);
                    return "reserved";
                }


                return "not available";
            } catch (BookNotFoundException | UserNotFoundException exception) {
                System.err.println(exception.getMessage());
                return exception.getMessage();
            } catch (SQLException sqlException) {
                System.err.println(sqlException.getSQLState());
                return "sql error";
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(exception);
            }
        }
    }

    private static User checkIfUserExists(long user_id) throws UserNotFoundException, SQLException {
        User user = UserRepository.getInstance().findUserById(user_id);
        if (user == null) {
            throw new UserNotFoundException("User not found!");
        }

        return user;
    }

    private static Book checkIfBookExists(long book_id) throws BookNotFoundException, SQLException {
        Book book = BookRepository.getInstance().findBookById(book_id);
        if (book == null) {
            throw new BookNotFoundException("Book not found!");
        }

        return book;
    }

    private static boolean isBookAlreadyReserved(Book book) {
        return book.getStatus().equals("RESERVED");
    }

    private static void finalizeReserveBook(Book book, User user) throws SQLException, InterruptedException {
        RentRepository.getInstance().addRent(new Rent(user.getId(), book.getId(), "RESERVED"));
        book.setStatus("RESERVED");
        Thread.sleep(5000);
    }

    public static String cancelReservationOfBook(long user_id, long book_id) {
        try {
            User user = checkIfUserExists(user_id);
            Book book = checkIfBookExists(book_id);
            Rent rent = checkIfBookReservedByUser(user, book);

            rent.setStatus("CANCELLED");
            RentRepository.getInstance().cancelRent(rent);
            book.setStatus("AVAILABLE");
            return "canceled";

        } catch (RentNotFoundException | UserNotFoundException | BookNotFoundException exception) {
            System.err.println(exception.getMessage());
            return exception.getMessage();
        } catch (SQLException sqlException) {
            System.err.println(sqlException.getSQLState());
            return "sql error";
        }
    }

    private static Rent checkIfBookReservedByUser(User user, Book book) throws RentNotFoundException, SQLException {
        Rent rent = RentRepository.getInstance().findRentByUserAndBookAndStatus(user, book, "RESERVED");
        if (rent == null) {
            throw new RentNotFoundException("Rent not found!");
        }
        return rent;
    }

    public static List<Book> getAllBooksReservedAndAvailableForUser(long user_id){
        try {
            return BookRepository.getInstance().getBooks(user_id);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Collections.emptyList();
        }
    }

}
