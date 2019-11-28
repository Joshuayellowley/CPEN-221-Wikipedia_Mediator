package cpen221.mp3.wikimediator;

import java.util.ArrayList;
import java.util.List;

import cpen221.mp3.cache.Cache;
import cpen221.mp3.cache.Cacheable;
import fastily.jwiki.core.Wiki;
import fastily.jwiki.dwrap.Revision;



public class WikiMediator {

    /* TODO: Implement this datatype

        You must implement the methods with the exact signatures
        as provided in the statement for this mini-project.

        You must add method signatures even for the methods that you
        do not plan to implement. You should provide skeleton implementation
        for those methods, and the skeleton implementation could return
        values like null.

     */

    private Wiki wiki = new Wiki("en.wikipedia.org");
    private Cache cache = new Cache();

    public WikiMediator(){

    }



    public List<String> simpleSearch(String query, int limit){

        return wiki.search(query, limit);
    }

    public String getPage(String pageTitle){

        return wiki.getPageText(pageTitle);
    }

    public List<String> getConnectedPages(String pageTitle, int hops){

        List<String> allPages = wiki.getLinksOnPage(pageTitle);

        int count = 1;

        while(count != hops){

            for(String s : allPages){
                List<String> tempList = wiki.getLinksOnPage(s);

                for(String s2 : tempList){
                    if(!allPages.contains(s2)){
                        allPages.add(s2);
                    }
                }
            }
            count++;
        }

        return allPages;
    }

    public List<String> zeitgeist(int limit){

        return null;
    }


}
