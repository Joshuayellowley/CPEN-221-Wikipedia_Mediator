package cpen221.mp3;

import cpen221.mp3.cache.Cache;
import cpen221.mp3.wikimediator.WikiMediator;
import fastily.jwiki.core.Wiki;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import static org.junit.Assert.assertEquals;

public class Tests {

    /*
        You can add your tests here.
        Remember to import the packages that you need, such
        as cpen221.mp3.cache.
     */

    @Test
    public void testGetPageText(){

        Wiki expected = new Wiki("en.wikipedia.com");
        expected.enableLogging(false);
        expected.search("Toot",10);
        WikiMediator w = new WikiMediator();
        System.out.println(w.getPage("Bear"));
        assertEquals(expected.search("Toot", 10),w.simpleSearch("Toot",10));
        assertEquals(expected.search("Butt",2),w.simpleSearch("Butt",2));
    }



    @Test
    public void testGetConnectedPages(){

        WikiMediator w = new WikiMediator();
        System.out.println(w.getConnectedPages("Bear", 1));
    }

    @Test
    public void testZeitgeist(){

        WikiMediator w = new WikiMediator();
        System.out.println(w.zeitgeist(2));
    }

    @Test
    public void testGetPageTextFromCache(){

        WikiMediator w = new WikiMediator();
        w.getPage("Bear");
        w.getPage("Boar");
        w.getPage("Beer");
        w.getPage("Bear");
        w.getPage("Beer");
        w.simpleSearch("Cheese",3);
    }

    @Test
    public void testTrending(){

        WikiMediator w = new WikiMediator();
        System.out.println(w.trending(1));
    }

    @Test
    public void testPeakLoad30s(){

        WikiMediator w = new WikiMediator();
//        try
//        {
////            Thread.sleep(30000);
//        }
//        catch(InterruptedException ex)
//        {
//            Thread.currentThread().interrupt();
//        }
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
        WikiMediator w = new WikiMediator();
        System.out.println(w.peakLoad30s());

    }




}
