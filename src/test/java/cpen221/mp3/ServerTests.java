package cpen221.mp3;

import com.google.gson.JsonObject;
import cpen221.mp3.server.WikiMediatorServer;
import cpen221.mp3.wikimediator.WikiMediator;
import fastily.jwiki.core.Wiki;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerTests {

    private String request(String test){
        try {
            WikiMediatorClient client = new WikiMediatorClient("localhost", WikiMediatorServer.WIKI_PORT);
            client.sendRequest(test);
            String reply = client.getReply();
            System.out.println(reply);
            client.close();
            return reply;

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return "";
    }

    @Test
    public void testSimpleSearch() throws InterruptedException {
        Thread t = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        String test = "{\n" +
                "\tid: \"1\",\n" +
                "\ttype: \"simpleSearch\",\n" +
                "\tquery: \"Barack Obama\",\n" +
                "\tlimit: \"100\"\n" +
                "}";

        request(test);
        t.interrupt();
    }



    @Test
    public void testGetPage() throws InterruptedException {
        Thread t = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        String test = "{\n" +
                "\tid: \"2\",\n" +
                "\ttype: \"getPage\",\n" +
                "\tpageTitle: \"Barack Obama\"\n" +
                "}";

        request(test);
        t.interrupt();
    }

    @Test
    public void testGetConnectedPages() throws InterruptedException {
        Thread t = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        String test = "{\n" +
                "\tid: \"3\",\n" +
                "\ttype: \"getConnectedPages\",\n" +
                "\tpageTitle: \"Barack Obama\",\n" +
                "\thops: \"1\"\n" +
                "}";

        request(test);
        t.interrupt();
    }

    @Test
    public void testZeitgeist() throws InterruptedException {
        Thread t = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        WikiMediator w = new WikiMediator();
        w.getPage("Beer");
        w.getPage("Beer");
        w.getPage("Beer");
        w.getPage("Beer");
        w.getPage("Boar");
        w.getPage("Boar");
        w.getPage("Boar");
        w.getPage("Bile");
        w.getPage("Bile");
        w.getPage("Beef");
        String test = "{\n" +
                "\tid: \"4\",\n" +
                "\ttype: \"zeitgeist\",\n" +
                "\tlimit: \"10\"\n" +
                "}";

        request(test);
        t.interrupt();
    }

    @Test
    public void testTrending() throws InterruptedException {
        Thread t = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        WikiMediator w = new WikiMediator();
        w.getPage("Beer");
        w.getPage("Beer");
        w.getPage("Beer");
        w.getPage("Beer");
        w.getPage("Boar");
        w.getPage("Boar");
        w.getPage("Boar");
        w.getPage("Bile");
        w.getPage("Bile");
        w.getPage("Beef");
        String test = "{\n" +
                "\tid: \"4\",\n" +
                "\ttype: \"trending\",\n" +
                "\tlimit: \"10\"\n" +
                "}";

        request(test);
        t.interrupt();
    }

    @Test
    public void testPeakLoad30s() throws InterruptedException {
        Thread t = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        WikiMediator w = new WikiMediator();
        w.getPage("Beer");
        w.getPage("Beer");
        w.getPage("Beer");
        w.getPage("Beer");
        w.getPage("Boar");
        w.getPage("Boar");
        w.getPage("Boar");
        w.getPage("Bile");
        w.getPage("Bile");
        w.getPage("Beef");
        String test = "{\n" +
                "\tid: \"4\",\n" +
                "\ttype: \"peakLoad30s\"\n" +
                "}";
        request(test);
        t.interrupt();
    }

    @Test
    public void testExecuteQuery() throws InterruptedException {
        Thread s = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        s.start();
        String test = "{\n" +
                "\tid: \"3\",\n" +
                "\ttype: \"executeQuery\",\n" +
                "\tquery: \"get page where category is \'Barack Obama\'\",\n" +
                "\ttimeout: \"60000000\"\n" +
                "}";
        request(test);
        s.interrupt();
    }

    @Test
    public void testExecuteQueryBadName() throws InterruptedException {
        Thread s = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        s.start();
        String test = "{\n" +
                "\tid: \"3\",\n" +
                "\ttype: \"executeQuery\",\n" +
                "\tquery: \"get page where category ifs \'Barack Obama\'\",\n" +
                "\ttimeout: \"60000000\"\n" +
                "}";
        request(test);
        s.interrupt();
    }
    @Test
    public void testExecuteQueryTimeout() throws InterruptedException {
        Thread s = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        s.start();
        String test = "{\n" +
                "\tid: \"3\",\n" +
                "\ttype: \"executeQuery\",\n" +
                "\tquery: \"get page where category ifs \'Barack Obama\'\",\n" +
                "\ttimeout: \"0\"\n" +
                "}";
        request(test);
        s.interrupt();
    }

    @Test
    public void testNoFunction() throws InterruptedException {
        Thread s = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        s.start();
        String test = "{\n" +
                "\tid: \"3\",\n" +
                "\ttype: \" \",\n" +
                "\tquery: \"get page where category ifs \'Barack Obama\'\",\n" +
                "\ttimeout: \"0\"\n" +
                "}";
        request(test);
        s.interrupt();
    }

    @Test
    public void timeout() throws InterruptedException {
        Thread s = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        s.start();
        String test = "{\n" +
                "\tid: \"3\",\n" +
                "\ttype: \"getConnectedPages\",\n" +
                "\tpageTitle: \"Barack Obama\",\n" +
                "\thops: \"2\",\n" +
                "\ttimeout: \"2\"\n" +
                "}";

        request(test);
        s.interrupt();
    }

    @Test
    public void tooManyClients() throws InterruptedException {
        Thread s = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        s.start();
        String test = "{\n" +
                "\tid: \"3\",\n" +
                "\ttype: \"getConnectedPages\",\n" +
                "\tpageTitle: \"Barack Obama\",\n" +
                "\thops: \"2\",\n" +
                "\ttimeout: \"1\"\n" +
                "}";

        Thread t = new Thread(()->request(test));
        Thread t1 = new Thread(()->request(test));
        Thread t2 = new Thread(()->request(test));
        Thread t3 = new Thread(()->request(test));
        Thread t4 = new Thread(()->request(test));
        Thread t5 = new Thread(()->request(test));
        Thread t6 = new Thread(()->request(test));

        t.start();
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();

        t.join();
        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();
        t6.join();
        s.interrupt();
    }

    @Test
    public void testSSTimeout() throws InterruptedException {
        Thread t = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        String test = "{\n" +
                "\tid: \"1\",\n" +
                "\ttype: \"simpleSearch\",\n" +
                "\tquery: \"A\",\n" +
                "\tlimit: \"10\",\n" +
                "\ttimeout: \"1\"\n" +
                "}";

        request(test);
        t.interrupt();
    }

    @Test
    public void testGetPageTimeout() throws InterruptedException {
        Thread t = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        String test = "{\n" +
                "\tid: \"1\",\n" +
                "\ttype: \"getPage\",\n" +
                "\tpageTitle: \"A\",\n" +
                "\ttimeout: \"0\"\n" +
                "}";

        request(test);
        t.interrupt();
    }

    @Test
    public void testZeitgeistTimeout() throws InterruptedException {
        Thread t = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        String test = "{\n" +
                "\tid: \"1\",\n" +
                "\ttype: \"zeitgeist\",\n" +
                "\tlimit: \"1\",\n" +
                "\ttimeout: \"0\"\n" +
                "}";

        request(test);
        t.interrupt();
    }

    @Test
    public void testTrendingTimeout() throws InterruptedException {
        Thread t = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        String test = "{\n" +
                "\tid: \"1\",\n" +
                "\ttype: \"trending\",\n" +
                "\tlimit: \"1\",\n" +
                "\ttimeout: \"0\"\n" +
                "}";

        request(test);
        t.interrupt();
    }

    @Test
    public void testPL30sTimeout() throws InterruptedException {
        Thread t = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        String test = "{\n" +
                "\tid: \"1\",\n" +
                "\ttype: \"peakLoad30s\",\n" +
                "\tlimit: \"1\",\n" +
                "\ttimeout: \"0\"\n" +
                "}";

        request(test);
        t.interrupt();
    }

    @Test
    public void testGetPathTimeout() throws InterruptedException {
        Thread t = new Thread(()->{
            try {
                WikiMediatorServer server = new WikiMediatorServer(3, WikiMediatorServer.WIKI_PORT);
                server.serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        String test = "{\n" +
                "\tid: \"1\",\n" +
                "\ttype: \"getPath\",\n" +
                "\tstartPage: \"1\",\n" +
                "\tstopPage: \"0\",\n" +
                "\ttimeout: \"0\"\n" +
                "}";

        request(test);
        t.interrupt();
    }


}

