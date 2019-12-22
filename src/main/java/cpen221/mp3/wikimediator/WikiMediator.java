package cpen221.mp3.wikimediator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import cpen221.mp3.cache.Cache;
import cpen221.mp3.wikimediator.WikiPage;
import cpen221.mp3.cache.Cacheable;
import fastily.jwiki.core.Wiki;
import fastily.jwiki.dwrap.Revision;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;

import javax.management.Query;

/**
 * Representation Invariant:
 *  requestTimes, lastAccessed, timesAccessed, wiki, and cache not null.
 *  For all Instants i in requestTimes, i < Instant.now()
 *  For all values i in lastAccessed, i < Instant.now()
 *  For all ints i in timesAccessed, i > 0
 *  lastAccessed.size() == timesAccessed.size()
 *  lastAccessed.contains(String s) == timesAccessed.contains(String s)
 *  wiki has the domain en.wikipedia.org
 *  cache has a capacity of 256 and a timeout of 12*60*60 seconds
 *
 * Abstraction Function:
 *  Represents a Wiki that allows for more functionality of
 *  searching the en.wikipedia.org search directory and accessing page text
 *  and information of recent searches.
 *  requestTimes.size() = the total number of requests on any instance of WikiMediator
 *  Instant within requestTimes is the time of a basic page request and is not null
 *  lastAccessed maps a String representing a wikipedia page title to the last time it was accessed
 *  timesAccessed maps a String representing a wikipedia page title to the amount of times it has been accessed
 *  wiki provides access to en.wikipedia.org and access to the search function and page text and page links on
 *  wikipedia.org
 *
 */
public class WikiMediator {

    private static List<Instant> requestTimes = new ArrayList<>();
    private static HashMap<String,Instant> lastAccessed = new HashMap<>();
    //private static HashMap<String,Integer> last30Secs = new HashMap<>();
    private static HashMap<String,Integer> timesAccessed = new HashMap<>();
    private Wiki wiki = new Wiki("en.wikipedia.org");
    private Cache cache = new Cache(256, 12*60*60);

    public WikiMediator(){
    }


    /**
     * Helper method used to update the last time an object in the cache has been updated.
     * Adds 1 to the amount of times an object has been accessed while within the cache.
     * Updates the last accessed time to the current time.
     *
     * @param id denoting the object in the cache that is being accessed
     * @mutates timesAccessed, lastAccessed
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
     *
     * @mutates this.requestTimes
     */
    private void addRequest(){
        requestTimes.add(Instant.now());
    }

    /**
     * Helper method used to log all statistics gathered by the implementation and save onto this device's harddrive.
     */
    private void logData(){
        try {
            FileWriter fileWriter = new FileWriter("local/data");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("Times Accessed");
            printWriter.println("______________");
            for(Map.Entry<String,Integer> e: timesAccessed.entrySet()){
                printWriter.println(e.getKey() + ": "+ e.getValue());
            }
            printWriter.println("Last Accessed");
            printWriter.println("______________");
            for(Map.Entry<String,Instant> e: lastAccessed.entrySet()){
                printWriter.println(e.getKey() + ": "+ e.getValue());
            }

            printWriter.println("Request Times");
            printWriter.println("______________");
            for(Instant i: requestTimes){
                printWriter.println(i);
            }
            printWriter.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * Searches on en.wikipedia.org for relevant pages to the given String query.
     * The results will be returned in terms of most relevant in non-ascending order.
     * The amount of items returned will be the size of given limit.
     *
     * @param query is the String to be entered into the wikipedia search algorithm
     * @param limit the amount of pages to return
     * @return a list of Strings representing the relevant pages in non-ascending order
     * @mutates this.timesAccessed, this.lastAccessed, this.requestTimes
     */
    public List<String> simpleSearch(String query, int limit){
        addRequest();
        updateAccess(query);
        logData();
        return wiki.search(query, limit);
    }




    /**
     * Given a page title on en.wikipedia.org, returns the correlating page text.
     * If the page is stored in the cache, takes the pageText from the cache instead of
     * accessing wikipedia again.
     *
     * @param pageTitle is the page title of the page with the desired text
     *
     * @return a String of the desired page text.  If there is no page of the specified
     *         pageTitle returns an empty String
     * @mutates this.timesAccessed, this.lastAccessed, this.cache, this.requestTimes
     */
    public String getPage(String pageTitle) {

        addRequest();
        updateAccess(pageTitle);
        logData();

        try {
            WikiPage toGet = (WikiPage) cache.get(pageTitle);
            return toGet.pageText();
        } catch (NoSuchElementException e){
            WikiPage toGet = new WikiPage(pageTitle);
            cache.put(toGet);
            String pageText = wiki.getPageText(pageTitle);
            return pageText;
         }
    }


    /**
     * Given a page title on en.wikipedia.org, returns the correlating page text.
     * If the page is stored in the cache, takes the pageText from the cache instead of
     * accessing wikipedia again.
     *
     * @param pageTitle is the page title of the page to start at.
     * @param hops the amount of jumps from the original webpage that can be made
     *
     * @return a List of Strings of pages that can be accessed through
     *         a given amount of hops.  If the given pageTitle is not a valid wikipedia page
     *         on en.wikipedia.org an empty list is returned.
     * @mutates this.requestTimes
     */
    public List<String> getConnectedPages(String pageTitle, int hops){

        addRequest();

        if(!wiki.exists(pageTitle)){
            return new ArrayList<>();
        }

        if(hops == 0){
            List<String> single = new ArrayList<>();
            single.add(pageTitle);
            return single;
        }

        List<String> allPages = wiki.getLinksOnPage(pageTitle);
        allPages.add(pageTitle);

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

    /**
     * Given a limit of the maximum amount of items to return in a list, returns
     * the most commonly used Strings in getPage and simpleSearch requests in
     * non-increasing order of frequency.
     *
     * @param limit > 0, the maximum size of the list of Strings to be returned.
     *
     * @return a List of Strings sorted by the amount of times the String has been
     *         used in simpleSearch and getPage requests.  The list is sorted in
     *         non-increasing order of count.
     * @mutates this.requestTimes
     */
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


    /**
     * Given a limit of the maximum amount of items to return in a list, returns
     * the most commonly used Strings in getPage and simpleSearch requests in
     * non-increasing order of frequency.
     *
     * @param limit > 0, the maximum size of the list of Strings to be returned.
     *
     * @return a List of Strings sorted by the amount of times the String has been
     *         used in simpleSearch and getPage requests.  The list is sorted in
     *         non-increasing order of count.
     * @mutates this.requestTimes
     */
    public List<String> trending(int limit){

        addRequest();
        List<String> result = new ArrayList<>();

        HashMap<String,Instant> start = (HashMap<String,Instant>) lastAccessed.clone();
        HashMap<String,Long> timeMap = new HashMap<>();

        for(String s : start.keySet()){
            timeMap.put(s, Duration.between(start.get(s),Instant.now()).toSeconds());
        }

        timeMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() < 30)
                .sorted(HashMap.Entry.comparingByValue(Comparator.naturalOrder()))
                .limit(limit)
                .forEachOrdered(x -> result.add(x.getKey()));

        return result;
    }

    /**
     * Looking at all instances of WikiMediator, returns an integer
     * correlating to the highest amount basic page requests that occurred
     * in any 30 second period.
     *
     * @return an int of the highest amount of basic requests to occur in any
     *         30 second period.
     * @mutates this.requestTimes
     */
    public int peakLoad30s(){

        addRequest();

        int count = 0;
        int max = 0;

        for(int i = 0; i < requestTimes.size(); i++){
            Instant start = requestTimes.get(i);
            count = 1;
            for(int p = i+1; p < requestTimes.size() - i; p++){

                if(Duration.between(start,requestTimes.get(p)).toSeconds() <= 30){
                    count++;
                    if(count > max){
                        max = count;
                    }
                }
            }
        }

        return max;
    }

    /**
     * Gets a path from startPage to stopPage, if one exists.
     * @param startPage, the id of the page to start at
     * @param stopPage, the id of the page to end at.
     * @return a list of page ids starting with startPage, and ending with stopPage.
     *          Each page is connected to the following page in the path. If startPage/stopPage
     *          is not a valid page, or no path exists, then return an empty list.
     */
    public List<String> getPath(String startPage, String stopPage){

        Instant begin = Instant.now();
        boolean foundIt = false;

        if(!wiki.exists(startPage) || !wiki.exists(stopPage)){
            return new ArrayList<>();
        }

        if(startPage.equals(stopPage)){
            List<String> single = new ArrayList<>();
            single.add(startPage);
            return single;
        }

        List<String> startList = wiki.getLinksOnPage(startPage);
        Set<String> Hop2List = new HashSet<>();
        List<String> stopList = wiki.getLinksOnPage(stopPage);
        List<String> result = new ArrayList<>();

        result.add(startPage);

        for(String s: startList){

            if(checkForPage(s,stopPage)){
                result.add(s);
                result.add(stopPage);
                return result;
            }

            Hop2List.addAll(wiki.getLinksOnPage(s));

            if(Duration.between(begin,Instant.now()).toSeconds() >= 280){
                return new ArrayList<>();
            }
        }

        return new ArrayList<>();
    }

    private boolean checkForPage(String p1, String p2){
        return wiki.getLinksOnPage(p1).contains(p2);
    }

    /**
     * Executes a detailed search given a query containing conditions of the search
     * @param query, the string to specify which pages to return
     * @return a list of page ids that correspond to the search query. If the query
     * does not follow the proper grammar, return an empty List.
     */
    public List<String> executeQuery(String query) throws InvalidQueryException{

        CharStream stream = new ANTLRInputStream(query);
        WikiQueryLexer lexer = new WikiQueryLexer(stream);
        lexer.reportErrorsAsExceptions();
        TokenStream tokens = new CommonTokenStream(lexer);

        // Feed the tokens into the parser.
        WikiQueryParser parser = new WikiQueryParser(tokens);
        parser.reportErrorsAsExceptions();

        // Generate the parse tree using the starter rule.
        try {
            ParseTree tree = parser.wikiquery(); // "root" is the starter rule.
        // debugging option #1: print the tree to the console
        System.err.println(tree.toStringTree(parser));

        // debugging option #2: show the tree in a window
        // ((RuleContext)tree).inspect(parser);

        // debugging option #3: walk the tree with a listener
        //new ParseTreeWalker().walk(new WikiQueryBaseListener(), tree);

        // Finally, construct a Poly value by walking over the parse tree.
        ParseTreeWalker walker = new ParseTreeWalker();
        WikiQueryBaseListener listener = new WikiQueryBaseListener();
        walker.walk(listener, tree);

        Token type = tokens.get(1);
        Token condition = tokens.get(3);
        Token sort = tokens.get(4);

        String t = type.getText();


        String returnType = "";

        if(t.charAt(0) == 'p') {
            returnType = "page";
        }

        if(t.charAt(0) == 'c'){
            returnType = "category";
        }

        if(t.charAt(0) == 'a'){
            returnType = "author";
        }

        QueryCondition eval = new QueryCondition(condition.getText());
        return evaluateConditions(returnType, eval, sort.getText());

        }catch (Exception e){
            throw new InvalidQueryException();
        }
    }

    private List<String> evaluateConditions(String returnType, QueryCondition eval, String sort){

        List<String> result = new ArrayList<>();

        if(returnType.equals("page")){
            result = evalForPage(eval);
        }

        if(returnType.equals("author")){
            result = evalForAuthor(eval);
        }

        if(returnType.equals("category")){
            result = evalForCategory(eval);
        }

        if(sort.equals("asc")){
            Collections.sort(result);
        }else if(sort.equals("desc")){
            Collections.sort(result, Collections.reverseOrder());
        }

        return result;
    }

    private List<String> evalForPage(QueryCondition eval){
        List<String> result = new ArrayList<>();
        List<String> fromLeft = new ArrayList<>();
        List<String> fromRight = new ArrayList<>();

        String search = "";
        if(!eval.compound){

            for(String s : eval.first.keySet()){
                search = s;
            }

            if(search.equals("category")){
                String temp = eval.first.get(search);
                result = wiki.getCategoryMembers(temp);
            }

            if(search.equals("title")){
                String temp = eval.first.get(search);
                if(wiki.exists(temp)) {
                    result.add(temp);
                }
            }

            if(search.equals("author")){
                String temp = eval.first.get(search);
                result.addAll(wiki.getUserUploads(temp));
            }

        }else{
            if(eval.left == null){
                for(String s : eval.first.keySet()){
                    search = s;
                }
                String temp = eval.first.get(search);

                if(search.equals("category")){
                    fromLeft = wiki.getCategoryMembers(temp);
                }

                if(search.equals("title")){
                    if(wiki.exists(temp)) {
                        fromLeft.add(temp);
                    }
                }

                if(search.equals("author")){
                    fromLeft.addAll(wiki.getUserUploads(temp));
                }
            }else{
                fromLeft = evalForPage(eval.left);
            }

            if(eval.right == null){
                for(String s : eval.second.keySet()){
                    search = s;
                }
                String temp = eval.second.get(search);

                if(search.equals("category")){
                    fromRight = wiki.getCategoryMembers(temp);
                }

                if(search.equals("title")){
                    if(wiki.exists(temp)) {
                        fromRight.add(temp);
                    }
                }

                if(search.equals("author")){
                    fromRight.addAll(wiki.getUserUploads(temp));
                }
            }else{
                fromRight = evalForPage(eval.right);
            }

            if(eval.type.equals("or")){
                result.addAll(fromLeft);
                result.addAll(fromRight);
            }

            if(eval.type.equals("and")){
                for(String s : fromLeft){
                    if(fromRight.contains(s)){
                        result.add(s);
                    }
                }
            }
        }

        return result;
    }

    private List<String> evalForAuthor(QueryCondition eval){
        List<String> result = new ArrayList<>();
        List<String> fromLeft = new ArrayList<>();
        List<String> fromRight = new ArrayList<>();
        String search = "";

        if(!eval.compound){

            for(String s : eval.first.keySet()){
                search = s;
            }

            if(search.equals("category")){
                String temp = eval.first.get(search);
                List<String> authorList = wiki.getCategoryMembers(temp);
                for(String s : authorList){
                    result.add(wiki.getLastEditor(s));
                }
            }

            if(search.equals("title")){
                String temp = eval.first.get(search);
                if(wiki.exists(temp)) {
                    result.add(wiki.getLastEditor(temp));
                }
            }

            if(search.equals("author")){
                result.add(eval.first.get(search));
            }

        }else{
            if(eval.left == null){
                for(String s : eval.first.keySet()){
                    search = s;
                }
                String temp = eval.first.get(search);

                if(search.equals("category")){
                    List<String> authorList = wiki.getCategoryMembers(temp);
                    for(String s : authorList){
                        fromLeft.add(wiki.getLastEditor(s));
                    }
                }

                if(search.equals("title")){
                    if(wiki.exists(temp)) {
                        fromLeft.add(wiki.getLastEditor(temp));
                    }
                }

                if(search.equals("author")){
                    fromLeft.add(temp);
                }
            }else{
                fromLeft = evalForAuthor(eval.left);
            }

            if(eval.right == null){
                for(String s : eval.second.keySet()){
                    search = s;
                }
                String temp = eval.second.get(search);

                if(search.equals("category")){
                    List<String> authorList = wiki.getCategoryMembers(temp);
                    for(String s : authorList){
                        fromRight.add(wiki.getLastEditor(s));
                    }
                }

                if(search.equals("title")){
                    if(wiki.exists(temp)) {
                        fromRight.add(wiki.getLastEditor(temp));
                    }
                }

                if(search.equals("author")){
                    fromRight.add(temp);
                }
            }else{
                fromRight = evalForAuthor(eval.right);
            }

            if(eval.type.equals("or")){
                result.addAll(fromLeft);
                result.addAll(fromRight);
            }

            if(eval.type.equals("and")){
                for(String s : fromLeft){
                    if(fromRight.contains(s)){
                        result.add(s);
                    }
                }
            }
        }

        return result;
    }

    private List<String> evalForCategory(QueryCondition eval){
        List<String> result = new ArrayList<>();
        List<String> fromLeft = new ArrayList<>();
        List<String> fromRight = new ArrayList<>();
        String search = "";

        if(!eval.compound){
            for(String s : eval.first.keySet()){
                search = s;
            }

            if(search.equals("category")){
                String temp = eval.first.get(search);
                result.add(temp);
            }

            if(search.equals("title")){
                String temp = eval.first.get(search);
                if(wiki.exists(temp)) {
                    result.addAll(wiki.getCategoriesOnPage(temp));
                }
            }

            if(search.equals("author")){
                String temp = eval.first.get(search);
                List<String> pageList = (wiki.getUserUploads(temp));
                for(String s : pageList){
                    result.addAll(wiki.getCategoriesOnPage(s));
                }

            }

        }else {
            if (eval.left == null) {
                for (String s : eval.first.keySet()) {
                    search = s;
                }
                String temp = eval.first.get(search);

                if (search.equals("category")) {
                    fromLeft.add(temp);
                }

                if (search.equals("title")) {
                    if (wiki.exists(temp)) {
                        fromLeft.addAll(wiki.getCategoriesOnPage(temp));
                    }
                }

                if (search.equals("author")) {
                    List<String> authorList = (wiki.getUserUploads(temp));
                    for (String s : authorList) {
                        fromLeft.addAll(wiki.getCategoriesOnPage(s));
                    }
                }
            } else {
                fromLeft = evalForCategory(eval.left);
            }

            if (eval.right == null) {
                for (String s : eval.second.keySet()) {
                    search = s;
                }
                String temp = eval.second.get(search);

                if (search.equals("category")) {
                    fromRight.add(temp);
                }

                if (search.equals("title")) {
                    if (wiki.exists(temp)) {
                        fromRight.addAll(wiki.getCategoriesOnPage(temp));
                    }
                }

                if (search.equals("author")) {
                    List<String> authorList = (wiki.getUserUploads(temp));
                    for (String s : authorList) {
                        fromRight.addAll(wiki.getCategoriesOnPage(s));
                    }
                }
                } else {
                    fromRight = evalForCategory(eval.right);
                }

                if (eval.type.equals("or")) {
                    result.addAll(fromLeft);
                    result.addAll(fromRight);
                }

                if (eval.type.equals("and")) {
                    for (String s : fromLeft) {
                        if (fromRight.contains(s)) {
                            result.add(s);
                        }
                    }
                }
            }

        return result;
    }
}


