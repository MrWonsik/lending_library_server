package connectorSample;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine;
        while((inputLine = in.readLine()) != null){
            if (".".equals(inputLine)){
                out.println("good bye");
                break;
            }
            out.println(inputLine);
        }

    }

    public static void main(String[] args) {
        EchoServer echoServer = new EchoServer();
        try {
            echoServer.start(6666);
        } catch (IOException e){
            System.err.println(e);
        }
    }
}
