package cpen221.mp3.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WikiMediatorServer {

    /**
     * Start a server at a given port number, with the ability to process
     * upto n requests concurrently.
     *
     * @param port the port number to bind the server to
     * @param n the number of concurrent requests the server can handle
     */

    private ServerSocket serverSocket;

    public WikiMediatorServer(int port, int n) throws IOException {
        /* TODO: Implement this method */
        serverSocket = new ServerSocket(port);
    }

}
