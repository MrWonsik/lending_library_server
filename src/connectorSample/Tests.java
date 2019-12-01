package connectorSample;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Tests {

    private EchoClient echoClient;

    @Before
    public void setup() throws IOException {
        echoClient = new EchoClient();
        echoClient.startConnection("localhost", 6666);
    }

    @After
    public void tearDown() throws IOException {
        echoClient.stopConnection();
    }

    @Test
    public void givenClient_whenServerEchosMessage_thenCorrect() throws IOException {
        String resp1 = echoClient.sendMessage("hello");
        String resp2 = echoClient.sendMessage("world");
        String resp3 = echoClient.sendMessage("!");
        String resp4 = echoClient.sendMessage(".");

        assertEquals("hello", resp1);
        assertEquals("world", resp2);
        assertEquals("!", resp3);
        assertEquals("good bye", resp4);
    }

    @Test
    public void givernGreetingClient_whenServerRespondsWhenStarted_thenCorrect() throws IOException{
        GreetClient client = new GreetClient();
        client.startConnection("localhost", 5000);
        String response = client.sendMessage("hello server");
        assertEquals("hello client", response);
    }

    @Test
    public void givenClient1_whenServerRespondes_thenCorrect() throws IOException{
        EchoClient client1 = new EchoClient();
        client1.startConnection("localhost", 6666);
        String msg1 = client1.sendMessage("hello");
        String msg2 = client1.sendMessage("world");
        String terminate = client1.sendMessage(".");

        assertEquals(msg1, "hello");
        assertEquals(msg2, "world");
        assertEquals(terminate, "bye");
    }

    @Test
    public void givenClient2_whenServerRespondes_thenCorrect() throws IOException{
        EchoClient client2 = new EchoClient();
        client2.startConnection("localhost", 6666);
        String msg1 = client2.sendMessage("hello");
        String msg2 = client2.sendMessage("world");
        String terminate = client2.sendMessage(".");

        assertEquals(msg1, "hello");
        assertEquals(msg2, "world");
        assertEquals(terminate, "bye");
    }

}
