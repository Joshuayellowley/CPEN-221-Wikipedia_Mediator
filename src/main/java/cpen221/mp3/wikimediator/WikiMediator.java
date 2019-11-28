package cpen221.mp3.wikimediator;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import cpen221.mp3.cache.Cache;
import cpen221.mp3.wikimediator.WikiPage;
import cpen221.mp3.cache.Cacheable;
import fastily.jwiki.core.Wiki;
import fastily.jwiki.dwrap.Revision;



public class WikiMediator {

    /* TODO: Implement this datatype
    // TODO: ALL METHODS R PUBLIC

        You must implement the methods with the exact signatures
        as provided in the statement for this mini-project.

        You must add method signatures even for the methods that you
        do not plan to implement. You should provide skeleton implementation
        for those methods, and the skeleton implementation could return
        values like null.

     */

    private static HashMap<String,Instant> lastAccessed = new HashMap<>();
    private static HashMap<String,Integer> timesAccessed = new HashMap<>();
    private Wiki wiki = new Wiki("en.wikipedia.org");
    private Cache cache = new Cache();

    public WikiMediator(){

    }


    private void updateAccess(String id){

        if(!timesAccessed.containsKey(id)){
            timesAccessed.put(id, 0);
        }else{
            timesAccessed.replace(id,timesAccessed.get(id) + 1);
        }

        if(!lastAccessed.containsKey(id)){
            lastAccessed.put(id, Instant.now());
        }else{
            lastAccessed.replace(id, Instant.now());
        }

    }


    public List<String> simpleSearch(String query, int limit){

        updateAccess(query);
        return wiki.search(query, limit);
    }

    public String getPage(String pageTitle) {

        updateAccess(pageTitle);

        try {
            WikiPage toGet = (WikiPage) cache.get(pageTitle);
            return toGet.pageText();
        } catch (Exception e){
            WikiPage toGet = new WikiPage(pageTitle);
            cache.put(toGet);
            return wiki.getPageText(pageTitle);
         }
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

    //TODO
    public List<String> zeitgeist(int limit){

        HashMap<String,Integer> start = (HashMap<String,Integer>) timesAccessed.clone();
        List<String> result = new ArrayList<>();

        start.entrySet()
                .stream()
                .sorted(HashMap.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> result.add(x.getKey()));

        return result;
    }

    public List<String> trending(int limit){
        List<String> result = new ArrayList<>();

        HashMap<String,Instant> start = (HashMap<String,Instant>) lastAccessed.clone();
        HashMap<String,Long> timeMap = new HashMap<>();

        for(String s : start.keySet()){
            timeMap.put(s, Duration.between(start.get(s),Instant.now()).toMillis());
        }

        timeMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() < 30000)
                .sorted(HashMap.Entry.comparingByValue(Comparator.naturalOrder()))
                .forEachOrdered(x -> result.add(x.getKey()));

        return result;
    }

}
