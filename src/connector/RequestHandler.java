package connector;


import controller.BookReservation;
import controller.UserAuthorization;
import model.Book;
import repository.BookRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

public class RequestHandler extends Thread {
    private Socket connection;
    private BufferedReader in;
    private PrintWriter out;


    protected RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(connection.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String[] request = inputLine.split(";");
                for (int i = 0; i < request.length; i++) {
                    System.out.println("request[" + i + "] " + request[i]);
                }

                if (".".equals(inputLine)) {
                    out.println("bye");
                    break;
                }

                if ("reserve".equals(request[0])) {
                    long user_id = Long.parseLong(request[1]);
                    long book_id = Long.parseLong(request[2]);
                    out.println(BookReservation.reserveBook(user_id, book_id));
                }

                if ("cancel".equals(request[0])) {
                     long user_id = Long.parseLong(request[1]);
                     long book_id = Long.parseLong(request[2]);
                     out.println(BookReservation.cancelReservationOfBook(user_id, book_id));
                }

                if ("getBooks".equals(request[0])) {
                    List<Book> availableBooks = BookRepository.getInstance().getBooks()
                            .stream()
                            .filter(book -> book.getStatus().equals("AVAILABLE"))
                            .collect(Collectors.toList());
                    out.println("get book");
                    for(Book book : availableBooks){
                        out.println(book);
                    }
                    out.println("over get book");
                }

                if ("login".equals(request[0])) {
                    String email = request[1];
                    String hash = request[2];
                    out.println(UserAuthorization.authoriseUser(email, hash));
                }
            }

            in.close();
            out.close();
            connection.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}


