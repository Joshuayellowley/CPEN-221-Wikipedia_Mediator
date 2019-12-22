package cpen221.mp3;

import com.google.gson.JsonObject;
import cpen221.mp3.server.WikiMediatorServer;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.IllegalFormatException;

/**
 * FibonacciClient is a client that sends requests to the FibonacciServer
 * and interprets its replies.
 * A new FibonacciClient is "open" until the close() method is called,
 * at which point it is "closed" and may not be used further.
 */
public class WikiMediatorClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    // Rep invariant: socket, in, out != null

    /**
     * Make a FibonacciClient and connect it to a server running on
     * hostname at the specified port.
     * @throws IOException if can't connect
     */
    public WikiMediatorClient(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Send a request to the server. Requires this is "open".
     * @param jsonObjString string to request a task from the server
     * @throws IOException if network or server failure
     */
    public void sendRequest(String jsonObjString) throws IOException {
        out.print(jsonObjString + "\n");
        out.flush(); // important! make sure x actually gets sent
    }

    /**
     * Get a reply from the next request that was submitted.
     * Requires this is "open".
     * @return the requested Fibonacci number
     * @throws IOException if network or server failure
     */
    public String getReply() throws IOException {
        String reply = in.readLine();
        if (reply == null) {
            throw new IOException("connection terminated unexpectedly");
        }

        try {
            return reply;
        } catch (IllegalFormatException e) {
            throw new IOException("misformatted reply: " + reply);
        }
    }

    /**
     * Closes the client's connection to the server.
     * This client is now "closed". Requires this is "open".
     * @throws IOException if close fails
     */
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }



    private static final int N = 7;
    /**
     * Use a WikiMediatorServer to serve N requests
     */
    public static void main(String[] args) {
        try {
                WikiMediatorClient client = new WikiMediatorClient("localhost", WikiMediatorServer.WIKI_PORT);
                String test = "{\n" +
                        "\tid: \"1\",\n" +
                        "\ttype: \"simpleSearch\",\n" +
                        "\tquery: \"Barack Obama\",\n" +
                        "\tlimit: \"100\"\n" +
                        "}";

                String test2 = "{\n" +
                        "\tid: \"3\",\n" +
                        "\ttype: \"getConnectedPages\",\n" +
                        "\tpageTitle: \"Barack Obama\",\n" +
                        "\thops: \"2\",\n" +
                        "\ttimeout: \"60000000\"\n" +
                        "}";
                client.sendRequest(test);
                String reply = client.getReply();
                System.out.println(reply);
                client.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

