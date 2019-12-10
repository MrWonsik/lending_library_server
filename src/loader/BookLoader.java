package loader;

import model.Book;
import repository.BookRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookLoader {

    public static void initBooks() {


        try {
            new Book("W pustyni i w puszczy", "Henryk Sienkiewicz", "Adventure", "2017", "Wydawnictwo GREG", 312);
            new Book("Przygody Tomka Sawyera", "Mark Twain", "Adventure", "2008", "Wydawnictwo GREG", 204);
            new Book("Dziewczyna z pociągu", "Paula Hawkins", "Thriller", "2017", "Świat Książki", 328);
            new Book("Ojciec chrzestny", "Mario Puzo", "Crime Story", "2019", "Wydawnictwo Albatros", 480);
            new Book("Mężczyzna z klasą", "Łukasz Kielban", "Guide", "2016", "Wydawnictwo ZNAK", 269);
            new Book("Sztuka kochania", "Magdalena Wisłocka", "Guide", "1984", "ISKRY", 284);
            new Book("Czasem czuły, czasem barbażyńca", "Tomasz Kwaśniewski", "Guide", "2016", "Wydawnictwo Agora", 270);
            new Book("Steve Jobs", "Walter Isaacson", "Biographical", "2011", "INSIGNIS", 735);
            new Book("Głaskologia", "Miłosz Brzeziński", "Guide", "2013", "Instytut Kreowania Skuteczności", 191);
            new Book("Czapkins - Historia Tomka Mackiewicza", "Dominik Szczepański", "Biographical", "2019", "Wydawnictwo Agora", 462);
            new Book("Czysty kod", "Robert C. Martin", "Scientific", "2014", "Helion", 423);
            new Book("Dywizjon 303", "Arkady Fiedler", "Historic", "1974", "Wydawnictwo Poznańskie", 115);
            new Book("Pięć tygodni w balonie", "Juliusz Verne", "Adventure", "1975", "Nasza Księgarnia", 298);
            new Book("Wojna grzechu", "Richard A. Knaak", "Adventure", "2006", "INSIGNIS", 356);
            new Book("Z klasą, na luzie", "Adam Jarczyński", "Guide", "2017", "Wydawnictwo ZNAK", 298);

        } catch (SQLException e) {
            System.err.println(e);
        }

        List<Book> books = BookRepository.getInstance().getBooks();

        System.out.println(books);
    }
}
