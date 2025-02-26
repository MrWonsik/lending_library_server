package test;

import controller.BookReservation;
import controller.UserAuthorization;
import model.Book;
import model.User;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;


//TODO: Więcej testów sprawdzających skrajne przypadki

public class ServerTest {

    private Book book;
    private User user;

    @Before
    public void setup() throws SQLException {
        book = new Book("Przykladowa ksiazka", "jakis autor", "jakas kategoria", "WYDAWNICTWO", "2019", 5);
        user = new User("imie", "nazwisko", "email", "city", "street", "postalCode", UserAuthorization.generateHash("Test123"));
    }

    @Test
    public void testGeneretingHash() {
        assertEquals("68eacb97d86f0c4621fa2b0e17cabd8c", user.getHash());
    }

    @Test
    public void reservationBookTest() {
        assertEquals("reserved", BookReservation.reserveBook(user.getId(), book.getId()));
    }

    @Test
    public void reservationAlreadyReservedBookTest() {
        BookReservation.reserveBook(user.getId(), book.getId());
        assertEquals("not available", BookReservation.reserveBook(user.getId(), book.getId()));
    }

    @Test
    public void reserveBookForUserThatNotExists() {
        assertEquals("User not found!", BookReservation.reserveBook(8888888L, book.getId()));
    }

    @Test
    public void reserveBookThatNotExists() {
        assertEquals("Book not found!", BookReservation.reserveBook(user.getId(), 88888888L));
    }

    @Test
    public void cancelReservationBook() {
        BookReservation.reserveBook(user.getId(), book.getId());
        assertEquals("canceled", BookReservation.cancelReservationOfBook(user.getId(), book.getId()));
    }

    @Test
    public void cancelReservationBookThatNotReservedByThisUser() {
        assertEquals("Rent not found!", BookReservation.cancelReservationOfBook(user.getId(), book.getId()));
    }

    @Test
    public void cancelReservationBookThatAlreadyCanceled() {
        BookReservation.reserveBook(user.getId(), book.getId());
        BookReservation.cancelReservationOfBook(user.getId(), book.getId());
        assertEquals("Rent not found!", BookReservation.cancelReservationOfBook(user.getId(), book.getId()));
    }

    @Test
    public void goodAuthoriseUser() {
        assertEquals("correct", UserAuthorization.authoriseUser("68eacb97d86f0c4621fa2b0e17cabd8c", "email"));
    }

    @Test
    public void badAuthoriseUser() {
        assertEquals("incorrect", UserAuthorization.authoriseUser("jakiszlyhash", "email"));
    }


}
