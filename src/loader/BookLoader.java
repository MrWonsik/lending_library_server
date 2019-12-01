package loader;

import database.DbConnector;
import model.Book;
import repository.BookRepository;

import java.sql.SQLException;
import java.util.List;

public class BookLoader {

    public static void initBooks() {


        try {
            Book book1 = new Book("W pustyni i w puszczy", "Henryk Sienkiewicz", "Adventure");
            Book book2 = new Book("Przygody Tomka Sawyera", "Mark Twain", "Adventure");
            Book book3 = new Book("Dziewczyna z pociągu", "Paula Hawkins", "Thriller");
            Book book4 = new Book("Ojciec chrzestny", "Mario Puzo", "Crime Story");
            Book book5 = new Book("Mężczyzna z klasą", "Łukasz Kielban", "Guide");
            Book book6 = new Book("Sztuka kochania", "Magdalena Wisłocka", "Guide");
            Book book7 = new Book("Czasem czuły, czasem barbażyńca", "Tomasz Kwaśniewski", "Guide");
        } catch (SQLException e) {
            System.err.println(e);
        }

        List<Book> books = BookRepository.getInstance().getBooks();

        System.out.println(books);
    }
}
