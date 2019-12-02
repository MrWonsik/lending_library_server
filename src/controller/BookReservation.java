package controller;

import model.Book;
import model.Rent;
import model.User;
import repository.BookRepository;
import repository.RentRepository;
import repository.UserRepository;

//TODO: poprawić Wyjątki, dodac własne

public class BookReservation {

    // TODO: DO OGARNIĘCIA W KONTEKSCIE JEDNEJ KSIAZKI!!!!
    public static synchronized String reserveBook(long user_id, long book_id) {
        try {

            Book book = checkIfBookExists(book_id);
            User user = checkIfUserExists(user_id);
            if (!isBookAlreadyReserved(book)) {
                RentRepository.getInstance().addRent(new Rent(user.getId(), book.getId(), "RESERVED"));
                book.setStatus("RESERVED");
                Thread.sleep(1000);
                return "reserved";
            }


            return "not available";
        } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
            return "smth wrong";
        }
    }

    private static synchronized void finalizeReserverBook(Book book, User user) throws Exception {

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

        } catch (Exception ex) {
            System.err.println(ex);
            return "smth wrong";
        }
    }

    private static User checkIfUserExists(long user_id) throws Exception {
        User user = UserRepository.getInstance().findUserById(user_id);
        if (user == null) {
            throw new Exception("User not found!");
        }

        return user;
    }

    private static Book checkIfBookExists(long book_id) throws Exception {
        Book book = BookRepository.getInstance().findBookById(book_id);
        if (book == null) {
            throw new Exception("Book not found!");
        }

        return book;
    }

    private static boolean isBookAlreadyReserved(Book book) {
        return book.getStatus().equals("RESERVED");
    }

    private static Rent checkIfBookReservedByUser(User user, Book book) throws Exception {
        Rent rent = RentRepository.getInstance().findRentByUserAndBookAndStatus(user, book, "RESERVED");
        if (rent == null) {
            throw new Exception("Rent not found!");
        }
        return rent;
    }

}
