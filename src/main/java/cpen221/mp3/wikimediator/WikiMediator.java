package cpen221.mp3.wikimediator;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

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

    private static List<Instant> requestTimes = new ArrayList<>();
    private static HashMap<String,Instant> lastAccessed = new HashMap<>();
    private static HashMap<String,Integer> timesAccessed = new HashMap<>();
    private Wiki wiki = new Wiki("en.wikipedia.org");
    private Cache cache = new Cache();

    public WikiMediator(){
    }


    /**
     * Helper method used to update the last time an object in the cache has been updated.
     * Adds 1 to the amount of times an object has been accessed while within the cache.
     * Updates the last accessed time to the current time.
     *
     * @param id denoting the object in the cache that is being accessed
     */
    private void updateAccess(String id){

        if(!timesAccessed.containsKey(id)){
            timesAccessed.put(id, 1);
        }else{
            timesAccessed.replace(id,timesAccessed.get(id) + 1);
        }

        if(!lastAccessed.containsKey(id)){
            lastAccessed.put(id, Instant.now());
        }else{
            lastAccessed.replace(id, Instant.now());
        }
    }

    /**
     * Helper method used in all basic requests to keep track of all requests made to the cache.
     * Adds an Instant to requestTimes at the current time.
     */
    private void addRequest(){
        requestTimes.add(Instant.now());
    }


    /**
     * Searches on en.wikipedia.org for relevant pages to the given String query.
     * The results will be returned in terms of most relevant in non-ascending order.
     * The amount of items returned will be the size of given limit.
     *
     * @param query is the String to be entered into the wikipedia search algorithm
     * @param limit the amount of pages to return
     * @return a list of Strings representing the relevant pages in non-ascending order
     */
    public List<String> simpleSearch(String query, int limit){

        addRequest();
        updateAccess(query);
        return wiki.search(query, limit);

    }

    public String getPage(String pageTitle) {

        addRequest();
        updateAccess(pageTitle);

        try {
            WikiPage toGet = (WikiPage) cache.get(pageTitle);
            return toGet.pageText();
        } catch (NoSuchElementException e){
            WikiPage toGet = new WikiPage(pageTitle);
            cache.put(toGet);
            return wiki.getPageText(pageTitle);
         }
    }


    public List<String> getConnectedPages(String pageTitle, int hops){

        addRequest();

        if(hops == 0){
            List<String> single = new ArrayList<>();
            single.add(pageTitle);
            return single;
        }

        List<String> allPages = wiki.getLinksOnPage(pageTitle);

        int count = 1;

        while(count != hops){
            List<String> toAdd = new ArrayList<>();
            for(String s : allPages){
                List<String> tempList = wiki.getLinksOnPage(s);
                for(String s2 : tempList){
                    if(!allPages.contains(s2)){
                        toAdd.add(s2);
                    }
                }
            }
            count++;
            allPages.addAll(toAdd);
        }

        return allPages;
    }

    //TODO
    public List<String> zeitgeist(int limit){

        addRequest();
        HashMap<String,Integer> start = (HashMap<String,Integer>) timesAccessed.clone();
        List<String> result = new ArrayList<>();

        start.entrySet()
                .stream()
                .sorted(HashMap.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .forEachOrdered(x -> result.add(x.getKey()));

        return result;
    }

    public List<String> trending(int limit){

        addRequest();
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
                .limit(limit)
                .forEachOrdered(x -> result.add(x.getKey()));

        return result;
    }

    public int peakLoad30s(){

        addRequest();

        int count = 0;
        int max = 0;

        for(int i = 0; i < requestTimes.size(); i++){
            Instant start = requestTimes.get(i);
            count = 0;
            for(int p = i+1; p < requestTimes.size() - i; p++){

                if(Duration.between(start,requestTimes.get(p)).toMillis() <= 1000*30){
                    count++;
                    if(count > max){
                        max = count;
                    }
                }
            }
        }

        return max;
    }

}
