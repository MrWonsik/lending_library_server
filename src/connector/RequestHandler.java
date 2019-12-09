package connector;


import controller.BookReservation;
import controller.UserAuthorization;
import controller.UserController;
import model.Book;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

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


            boolean isTerminated = false;
            String inputLine;

            while(!isTerminated){
                inputLine = in.readLine();
                String[] request = inputLine.split(";");
                for (int i = 0; i < request.length; i++) {
                    System.out.println("request[" + i + "] " + request[i]);
                }

                switch (request[0]) {
                    case ".": {
                        out.println("bye");
                        isTerminated = true;
                        break;
                    }
                    case "login": {
                        String email = request[1];
                        String hash = request[2];
                        out.println(UserAuthorization.authoriseUser(email, hash));
                        break;
                    }
                    case "getUserInfo": {
                        String email = request[1];
                        out.println(UserController.getUserInfo(email));
                        break;
                    }
                    case "reserve": {
                        long user_id = Long.parseLong(request[1]);
                        long book_id = Long.parseLong(request[2]);
                        out.println(BookReservation.reserveBook(user_id, book_id));
                        break;
                    }
                    case "cancel": {
                        long user_id = Long.parseLong(request[1]);
                        long book_id = Long.parseLong(request[2]);
                        out.println(BookReservation.cancelReservationOfBook(user_id, book_id));
                        break;
                    }
                    case "getBooks": {
                        long user_id = Long.parseLong(request[1]);
                        List<Book> usersBook = BookReservation.getAllBooksReservedAndAvailableForUser(user_id);
                        out.println("get book");
                        for (Book book : usersBook) {
                            out.println(book);
                        }
                        out.println("over get book");
                        break;
                    }
                    case "getBooksWithFilter": {
                        long user_id = Long.parseLong(request[1]);
                        String title = request[2];
                        String author = request[3];
                        String publishingHome = request[4];
                        String category = request[5];
                        List<Book> usersBook = BookReservation.getAllBooksReservedAndAvailableForUser(user_id, title, author, publishingHome, category);
                        out.println("get book");
                        for (Book book : usersBook) {
                            out.println(book);
                        }
                        out.println("over get book");
                        break;
                    }
                    default: {
                        out.println("request not found!");
                    }
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


