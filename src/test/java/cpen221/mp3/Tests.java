package cpen221.mp3;

import cpen221.mp3.cache.Cache;
import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class Tests {

    /*
        You can add your tests here.
        Remember to import the packages that you need, such
        as cpen221.mp3.cache.
     */

    private WikiMediator w = new WikiMediator();
    private WikiMediator w2 = new WikiMediator();

    @Test
    public void testGetPageText(){

        System.out.println(w.getPage("Bear"));
        w.simpleSearch("Toot",10);
        w.simpleSearch("Butt", 2);
    }

    @Test
    public void testGetPageText2(){
        assertEquals("",(w.getPage("BuBsbs")));
    }



    @Test
    public void testGetConnectedPages(){
        System.out.println(w.getConnectedPages("MissingNo.", 2));
    }

    @Test
    public void testGetConnectedPages2(){
       assertEquals(w.getConnectedPages("haHAH.", 1), new ArrayList<>());
    }

    @Test
    public void testZeitgeist(){

        System.out.println(w.zeitgeist(10));
    }

    @Test
    public void testGetPageTextFromCache(){

        w.getPage("Bear");
        w.getPage("Boar");
        w.getPage("Beer");
        w.getPage("Bear");
        w.getPage("Beer");
        w.simpleSearch("Cheese",3);
    }

    @Test
    public void testTrending(){

        System.out.println(w.trending(10));
    }

    @Test
    public void testPeakLoad30s(){

        try
        {
            Thread.sleep(30000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        System.out.println(w.peakLoad30s());
    }

    @Test
    public void instantTimeTesting(){
        Instant start = Instant.now();
        try
        {
            Thread.sleep(1000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        Instant end = Instant.now();

        System.out.println(Duration.between(start,end).toMillis());

    }

    @Test
    public void testBasicGetPage(){

        String s1 = "Canada";
        String s2 = "Mexico";
        WikiMediator w = new WikiMediator();

        assertEquals(w.getPath(s1,s2), new ArrayList<>());

    }

    @Test
    public void testExecuteQuery1(){

        String query = "get page where title is 'Canada'";
        WikiMediator w = new WikiMediator();

        List<String> expected = new ArrayList<>();
        expected.add("Canada");

        assertEquals(expected, w.executeQuery(query));

    }

    @Test
    public void testExecuteQuery2(){

        String query = "get page where (title is 'nintendo' or author is 'baby')";
        WikiMediator w = new WikiMediator();

        List<String> expected = (w.executeQuery(query));

        for(String s : expected){
            System.out.println(s);
        }

    }

    @Test
    public void testExecuteQuery3(){

        String query = "get page where category is 'Illinois state senators'";
        WikiMediator w = new WikiMediator();

        assertEquals(w.executeQuery(query), null);

    }

    @Test
    public void testExecuteQuery4(){

        String query = "get category where (author is 'CLCStudent' and (title is 'Barack Obama' or title is 'Naomi Klein'))";
        WikiMediator w = new WikiMediator();

        assertEquals(w.executeQuery(query), null);

    }

    @Test
    public void testExecuteQuery5(){

        String query = "get page where ((author is 'AndrewOne' and author is 'Sylas') or (title is 'Barack Obama' or category is 'Games'))";
        WikiMediator w = new WikiMediator();

        assertEquals(w.executeQuery(query), null);

    }


}
