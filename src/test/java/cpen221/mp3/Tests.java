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
    public void testPutInCache(){


    }


    @Test
    public void testSearch(){

        WikiMediator w = new WikiMediator();
        System.out.println(w.simpleSearch("Poop", 10));
    }

    @Test
    public void testGetPageText(){

        WikiMediator w = new WikiMediator();
        System.out.println(w.getPage("Bear"));
    }

    @Test
    public void testGetPossiblePages(){

        WikiMediator w = new WikiMediator();
        System.out.println(w.getConnectedPages("Bear", 1));
    }




}
