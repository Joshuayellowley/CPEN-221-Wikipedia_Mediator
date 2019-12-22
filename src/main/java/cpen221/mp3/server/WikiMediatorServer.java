package cpen221.mp3.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.IllegalFormatException;
import java.util.List;

import com.google.gson.*;
import cpen221.mp3.wikimediator.InvalidQueryException;
import cpen221.mp3.wikimediator.WikiMediator;

import java.util.IllegalFormatException;
import java.util.List;



/**
 * Representation Invariant:
 *  serverSocket != null
 *  maxClients >= 0
 *
 * Abstraction Function:
 *  Represents a server which accepts and handles
 *  up to maxRequests WikiMediator requests on the default port 5504.
 *  Requests must be in the form of a JSON formatted string, and must include a
 *  valid id, type, and parameters for the type. Optional: request may
 *  include a timeout field which indicates in seconds how long the service should wait
 *  for a response from Wikipedia before declaring the operation has failed.
 *  Response is in a JSON formatted string, specifying the id of the request,
 *  the status and a response field.
 *
 */

public class WikiMediatorServer {

    /** Default port number where the server listens for connections. */
    public static final int WIKI_PORT = 5504;

    private ServerSocket serverSocket;

    private int maxRequests;
    /**
     * Start a server at a given port number, with the ability to process
     * up to n requests concurrently.
     *
     * @param port the port number to bind the server to.
     *             requires 0 <= port <= 65535
     * @param n the number of concurrent requests the server can handle.
     *          requires n > 0
     */

    public WikiMediatorServer(int port, int n) throws IOException {
        serverSocket = new ServerSocket(port);
        maxRequests = n;
    }

    /**
     * Run the server, listening for connections and handling them.
     *
     * @throws IOException
     *             if the main server socket is broken
     */
    public void serve() throws IOException {
        while (true) {
            // block until a client connects
            final Socket socket = serverSocket.accept();
            // create a new thread to handle that client
            Thread handler = new Thread(() -> {
                try {
                    try {
                        handle(socket);
                    } finally {
                        socket.close();
                    }
                } catch (IOException ioe) {
                    // this exception wouldn't terminate serve(),
                    // since we're now on a different thread, but
                    // we still need to handle it
                    ioe.printStackTrace();
                }
            });
            // start the thread
            handler.start();
        }
    }


    /**
     * Handle one client connection. Returns when client disconnects.
     *
     * @param socket
     *            socket where client is connected
     * @throws IOException
     *             if connection encounters an error
     */
    private void handle(Socket socket) throws IOException {
        System.err.println("client connected");

        // get the socket's input stream, and wrap converters around it
        // that convert it from a byte stream to a character stream,
        // and that buffer it so that we can read a line at a time
        BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        // similarly, wrap character=>bytestream converter around the
        // socket output stream, and wrap a PrintWriter around that so
        // that we have more convenient ways to write Java primitive
        // types to it.
        PrintWriter out = new PrintWriter(new OutputStreamWriter(
                socket.getOutputStream()), true);

        try {
            // each request is a JSON object containing a number
            StringBuilder jsonString = new StringBuilder();
            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {
                jsonString.append(line);
            }
                mediate(jsonString);

        } finally {
            out.close();
            in.close();
        }
    }


    /**
     * Helper method that converts a list of Strings into a String[]
     * @param list the list of Strings to convert
     * @return an array of Strings, with list.get(i) == arr[i]
     */
    private String[] listToArray(List<String> list){
        String[] arr = new String[list.size()];

        for(int i = 0; i < list.size(); i++){
            arr[i] = list.get(i);
        }

        return arr;
    }

    private JsonObject mediate(StringBuilder jsonString) throws IllegalFormatException {
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(jsonString.toString());

        JsonElement id = json.get("id");
        JsonElement type = json.get("type");
        String function = type.getAsString();

        String query = "";
        int limit = 0;
        String pageTitle = "";
        int hops = 0;
        int timeout = Integer.MAX_VALUE;
        String startPage = "";
        String stopPage = "";

        if (json.has("startPage")){
            startPage = json.get("startPage").getAsString();
        }
        if (json.has("stopPage")){
            stopPage = json.get("stopPage").getAsString();
        }
        if(json.has("timeout")) {
            timeout = json.get("timeout").getAsInt();
        }
        if(json.has("query")){
            query = json.get("query").getAsString();
        }
        if(json.has("pageTitle")){
            pageTitle = json.get("pageTitle").getAsString();
        }
        if(json.has("hops")){
            hops = json.get("hops").getAsInt();
        }
        if(json.has("limit")){
            limit = json.get("limit").getAsInt();
        }

        WikiMediator mediator = new WikiMediator();

        JsonObject finished = new JsonObject();

        finished.add("id",id);
        Object response;
        //TODO: this is broken rn but we can fix in a bit
        if(function.equals("simpleSearch")){

            response = mediator.simpleSearch(query,limit);
        }
        else if(function.equals("getPage")){
            response = mediator.getPage(pageTitle);
        }
        else if(function.equals("getConnectedPages")){
            response = mediator.getConnectedPages(pageTitle,hops);
        }
        else if(function.equals("zeitgeist")){
            response = mediator.zeitgeist(limit);
        }
        else if(function.equals("trending")){
            response = mediator.trending(limit);
        }
        else if(function.equals("peakLoad30s")){
            response = mediator.peakLoad30s();
        }
        else if(function.equals("getPath")){
            response = mediator.getPath(startPage,stopPage);
        }
        else if(function.equals("executeQuery")){
            try {
                response = mediator.executeQuery(query);
            }catch (InvalidQueryException e){
                System.out.println("Invalid Query!");
            }
        }
        return null;
    }

}
