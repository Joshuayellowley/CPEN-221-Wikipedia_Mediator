package cpen221.mp3;

import cpen221.mp3.cache.Cache;
import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.Test;

public class Tests {

    /*
        You can add your tests here.
        Remember to import the packages that you need, such
        as cpen221.mp3.cache.
     */

    @Test
    public void testSearch(){

        WikiMediator w = new WikiMediator();
        w.simpleSearch("Butt", 2);

    }


    @Test
    public void testGetPageText(){

        WikiMediator w = new WikiMediator();
        System.out.println(w.getPage("Bear"));
        w.simpleSearch("Toot",10);
    }



    @Test
    public void testGetConnectedPages(){

        WikiMediator w = new WikiMediator();
        System.out.println(w.getConnectedPages("Bear", 1));
    }

    @Test
    public void testZeitgeist(){

        WikiMediator w = new WikiMediator();
        System.out.println(w.zeitgeist(10));
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
        System.out.println(w.trending(10));
    }


}
