package test;

import controller.BookReservation;
import controller.UserAuthorization;
import org.junit.Test;
import repository.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class ClientTest {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    @Test
    public void endConnection() throws IOException {
        ClientTest clientTest = new ClientTest();
        clientTest.startConnection("localhost", 5000);
        String terminate = clientTest.sendMessage(".");
        clientTest.stopConnection();
        assertEquals("bye", terminate);
    }

    @Test
    public void tryToRentABook() throws IOException {
        ClientTest clientTest = new ClientTest();
        clientTest.startConnection("localhost", 5000);
        String msg1 = clientTest.sendMessage("reserve;2;4");
        String terminate = clientTest.sendMessage(".");
        clientTest.stopConnection();
        assertEquals("reserved", msg1);
    }

    @Test
    public void tryToRentABookTwoTimes() throws IOException {
        ClientTest clientTest = new ClientTest();
        clientTest.startConnection("localhost", 5000);
        String msg1 = clientTest.sendMessage("reserve;2;7");
        String msg2 = clientTest.sendMessage("reserve;2;7");
        String terminate = clientTest.sendMessage(".");
        clientTest.stopConnection();
        assertEquals("not available", msg2);
    }

    @Test
    public void tryToRentABookAndCancelReservation() throws IOException {
        ClientTest clientTest = new ClientTest();
        clientTest.startConnection("localhost", 5000);
        String msg1 = clientTest.sendMessage("reserve;2;4");
        String msg2 = clientTest.sendMessage("cancel;2;4");
        String terminate = clientTest.sendMessage(".");
        clientTest.stopConnection();
        assertEquals("canceled", msg2);
    }

    @Test
    public void tryToGetBooks() throws IOException {
        ClientTest clientTest = new ClientTest();
        clientTest.startConnection("localhost", 5000);
        String msg1 = clientTest.sendMessage("getBooks");
        String book = "";
        if (msg1.equals("get book")) {
            while ((!book.equals("over get book"))) {
                book = clientTest.sendMessage("i got it!");
                System.out.println(book);
            }
        }
        clientTest.stopConnection();
        assertEquals("over get book", book);
    }

    @Test
    public void tryToLogin() throws IOException {
        ClientTest clientTest = new ClientTest();
        clientTest.startConnection("localhost", 5000);
        String hash = UserAuthorization.generateHash("Tomek1");
        String msg1 = clientTest.sendMessage("login;" + hash + ";tomek@gmail.com");
        clientTest.stopConnection();
        assertEquals("correct", msg1);
    }

    @Test
    public void parrallelTestReservationTheSameBookByFewUser() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for(int i=0;i<3;i++) {
            int finalI = (i%3)+1;
            Random random = new Random();
            Runnable parallelTask = () -> {
                try {
                    ClientTest clientTest = new ClientTest();
                    clientTest.startConnection("localhost", 5000);
                    String request = "reserve;"+ finalI +";"+(random.nextInt(7)+1);
                    //String request = "reserve;"+ finalI +";5";
                    System.out.println(LocalDateTime.now() + " request: " + request);
                    String msg1 = clientTest.sendMessage(request);
                    String terminate = clientTest.sendMessage(".");
                    System.out.println(LocalDateTime.now() + " response: " + msg1);
                    clientTest.stopConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        };
            executorService.submit(parallelTask);
        }

        try {
            executorService.awaitTermination(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }


}
