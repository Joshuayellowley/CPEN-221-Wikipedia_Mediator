package cpen221.mp3.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.concurrent.*;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import cpen221.mp3.wikimediator.InvalidQueryException;
import cpen221.mp3.wikimediator.WikiMediator;

import java.util.IllegalFormatException;
import java.util.List;



/**
 * Representation Invariant:
 * serverSocket != null
 * maxClients >= 0
 * <p>
 * Abstraction Function:
 * Represents a server which accepts and handles
 * up to maxRequests WikiMediator requests on the default port 5504.
 * Requests must be in the form of a JSON formatted string, and must include a
 * valid id, type, and parameters for the type. Optional: request may
 * include a timeout field which indicates in seconds how long the service should wait
 * for a response from Wikipedia before declaring the operation has failed.
 * Response is in a JSON formatted string, specifying the id of the request,
 * the status and a response field.
 */

public class WikiMediatorServer {

    /**
     * Default port number where the server listens for connections.
     */
    public static final int WIKI_PORT = 5504;

    private ServerSocket serverSocket;

    private int maxRequests;

    private int numClients = 0;

    /**
     * Start a server at a given port number, with the ability to process
     * up to n requests concurrently.
     *
     * @param port the port number to bind the server to.
     *             requires 0 <= port <= 65535
     * @param n    the number of concurrent requests the server can handle.
     *             requires n > 0
     */

    public WikiMediatorServer(int n, int port) throws IOException {
        serverSocket = new ServerSocket(port);
        maxRequests = n;
    }

    /**
     * Run the server, listening for connections and handling them.
     *
     * @throws IOException if the main server socket is broken
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
                numClients++;
        }
    }


    /**
     * Handle one client connection. Returns when client disconnects.
     *
     * @param socket socket where client is connected
     * @throws IOException if connection encounters an error
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
            // each request is a JSON object containing many lines indicating the request
            StringBuilder jsonString = new StringBuilder();
            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {
                System.out.println(line);
                jsonString.append(line);
                if (line.equals("}")) {
                    try {
                        if(numClients <= maxRequests) {
                            JsonObject jo = mediate(jsonString);
                            out.print(jo);
                        }
                        else{
                            System.err.println("reply: too many requests");
                            out.print("too many requests, please try again later.\n");
                        }
                    } catch (IllegalFormatException e) {
                        System.err.println("reply: err");
                        out.print("err\n");
                    }
                    out.flush();
                    System.out.println("flushed");
                    break;
                }
            }
        } finally {
            System.out.println("finished handling");
            out.close();
            in.close();
            numClients--;
        }
    }


    /**
     * Helper method that converts a list of Strings into a String[]
     *
     * @param list the list of Strings to convert
     * @return an array of Strings, with list.get(i) == arr[i]
     */
    private JsonArray listToJsonArr(List<String> list) {
        JsonArray arr = new JsonArray(list.size());

        for (String s : list) {
            arr.add(s);
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

        if (json.has("startPage")) {
            startPage = json.get("startPage").getAsString();
        }
        if (json.has("stopPage")) {
            stopPage = json.get("stopPage").getAsString();
        }
        if (json.has("timeout")) {
            timeout = json.get("timeout").getAsInt();
        }
        if (json.has("query")) {
            query = json.get("query").getAsString();
        }
        if (json.has("pageTitle")) {
            pageTitle = json.get("pageTitle").getAsString();
        }
        if (json.has("hops")) {
            hops = json.get("hops").getAsInt();
        }
        if (json.has("limit")) {
            limit = json.get("limit").getAsInt();
        }

        final int finalLimit = limit;
        final String finalQuery = query;
        final String finalPageTitle = pageTitle;
        final int finalHops = hops;
        final String finalStartPage = startPage;
        final String finalStopPage = stopPage;
        WikiMediator mediator = new WikiMediator();

        JsonObject finished = new JsonObject();

        finished.add("id", id);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        switch (function) {
            case "simpleSearch": {
                System.out.println("simple searching");
                Future<List<String>> future = executor.submit(() -> mediator.simpleSearch(finalQuery, finalLimit));
                try {
                    List<String> response = future.get(timeout, TimeUnit.SECONDS);
                    finished.add("status", new JsonPrimitive("success"));
                    finished.add("response", listToJsonArr(response));
                } catch (InterruptedException | ExecutionException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Unexpected Interruption"));
                } catch (TimeoutException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Operation timed out"));
                }
                System.out.println("done searching");
                break;
            }
            case "getPage": {
                System.out.println("getting page");
                Future<String> future = executor.submit(() -> mediator.getPage(finalPageTitle));
                try {
                    String response = future.get(timeout, TimeUnit.SECONDS);
                    finished.add("status", new JsonPrimitive("success"));
                    finished.add("response", new JsonPrimitive(response));
                } catch (InterruptedException | ExecutionException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Unexpected Interruption"));
                } catch (TimeoutException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Operation timed out"));
                }
                break;
            }
            case "getConnectedPages": {
                Future<List<String>> future = executor.submit(() -> mediator.getConnectedPages(finalPageTitle, finalHops));
                try {
                    List<String> response = future.get(timeout, TimeUnit.SECONDS);
                    finished.add("status", new JsonPrimitive("success"));
                    finished.add("response", listToJsonArr(response));
                } catch (InterruptedException | ExecutionException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Unexpected Interruption"));
                } catch (TimeoutException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Operation timed out"));
                }
                break;
            }
            case "zeitgeist": {
                Future<List<String>> future = executor.submit(() -> mediator.zeitgeist(finalLimit));
                try {
                    List<String> response = future.get(timeout, TimeUnit.SECONDS);
                    finished.add("status", new JsonPrimitive("success"));
                    finished.add("response", listToJsonArr(response));
                } catch (InterruptedException | ExecutionException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Unexpected Interruption"));
                } catch (TimeoutException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Operation timed out"));
                }
                break;
            }
            case "trending": {
                Future<List<String>> future = executor.submit(() -> mediator.trending(finalLimit));
                try {
                    List<String> response = future.get(timeout, TimeUnit.SECONDS);
                    finished.add("status", new JsonPrimitive("success"));
                    finished.add("response", listToJsonArr(response));
                } catch (InterruptedException | ExecutionException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Unexpected Interruption"));
                } catch (TimeoutException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Operation timed out"));
                }
                break;
            }
            case "peakLoad30s": {
                Future<Integer> future = executor.submit(() -> mediator.peakLoad30s());
                try {
                    Integer response = future.get(timeout, TimeUnit.SECONDS);
                    finished.add("status", new JsonPrimitive("success"));
                    finished.add("response", new JsonPrimitive(response));
                } catch (InterruptedException | ExecutionException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Unexpected Interruption"));
                } catch (TimeoutException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Operation timed out"));
                }
                break;
            }
            case "getPath": {
                Future<List<String>> future = executor.submit(() -> mediator.getPath(finalStartPage, finalStopPage));
                try {
                    List<String> response = future.get(timeout, TimeUnit.SECONDS);
                    finished.add("status", new JsonPrimitive("success"));
                    finished.add("response", listToJsonArr(response));
                } catch (InterruptedException | ExecutionException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Unexpected Interruption"));
                } catch (TimeoutException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Operation timed out"));
                }
                break;
            }
            case "executeQuery": {
                Future<List<String>> future = executor.submit(() -> mediator.executeQuery(finalQuery));
                try {
                    List<String> response = future.get(timeout, TimeUnit.SECONDS);
                    finished.add("status", new JsonPrimitive("success"));
                    finished.add("response", listToJsonArr(response));
                } catch (InterruptedException | ExecutionException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Unexpected Interruption"));
                } catch (TimeoutException e) {
                    finished.add("status", new JsonPrimitive("failed"));
                    finished.add("response", new JsonPrimitive("Operation timed out"));
                }
                break;
            }
        }
        return finished;
    }

    public static void main(String[] args) {
        try {
            WikiMediatorServer server = new WikiMediatorServer(3, WIKI_PORT);
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
