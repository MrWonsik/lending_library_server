package connector;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerTCP {

    private ServerSocket serverSocket;
    private boolean serverIsRunning;

   public void runServer(int port) throws IOException{
       serverSocket = new ServerSocket(port);
       System.out.println("Server started");
       serverIsRunning = true;
       while(true){
           new RequestHandler(serverSocket.accept()).start();
       }
    }

    public void stop() throws IOException {
       if(serverIsRunning) {
           System.out.println("Server stoped");
           serverSocket.close();
       }
    }



}
