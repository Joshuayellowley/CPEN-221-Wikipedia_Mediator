package cpen221.mp3;

import cpen221.mp3.cache.Cache;
import cpen221.mp3.wikimediator.InvalidQueryException;
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
    public void testGetConnectedPages3(){
        List<String> result =  new ArrayList<>();
        result.add("Grape");
        assertEquals(w.getConnectedPages("Grape", 0), result);
    }

    @Test
    public void testZeitgeist(){


        System.out.println(w.getPage("Bear"));
        w.simpleSearch("Toot",10);
        w.simpleSearch("Butter", 2);
        w.simpleSearch("Tree",10);
        w.simpleSearch("Butter", 2);
        w.simpleSearch("Toot",10);
        w.simpleSearch("Toot", 2);
        w.simpleSearch("Bear",10);
        w.simpleSearch("Bear", 2);
        System.out.println(w.zeitgeist(4));
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
    public void testBasicGetPath(){

        String s1 = "Canada";
        String s2 = "Mexico";
        WikiMediator w = new WikiMediator();

        for(String s :(w.getPath(s1,s2))){
            System.out.println(s);
        }
    }

    @Test
    public void testBasicGetPath2(){
        String s1 = "Canada";
        String s2 = "Canada";
        WikiMediator w = new WikiMediator();

        for(String s :(w.getPath(s1,s2))){
            System.out.println(s);
        }
    }

    @Test
    public void testBasicGetPath3(){

        String s1 = "BaddUm";
        String s2 = "Canada";
        WikiMediator w = new WikiMediator();

        for(String s :(w.getPath(s1,s2))){
            System.out.println(s);
        }
    }


    @Test
    public void testInvalidExecuteQuery(){

        String query = "but page where title is 'Canada'";
        WikiMediator w = new WikiMediator();

        List<String> expected = new ArrayList<>();
        expected.add("Canada");
        try {
            w.executeQuery(query);
            fail("Should have thrown an exception.");
        }catch (InvalidQueryException e) { }
    }

    @Test
    public void testExecuteQuery1(){

        String query = "get page where title is 'Canada'";
        WikiMediator w = new WikiMediator();

        List<String> expected = new ArrayList<>();
        expected.add("Canada");

        try {
            assertEquals(expected, w.executeQuery(query));
        }catch (InvalidQueryException e){
            fail();
        }
    }

    @Test
    public void testExecuteQuery2(){

        String query = "get author where (title is 'Nintendo' or category is 'Super Mario') desc";
        WikiMediator w = new WikiMediator();

        try{
        List<String> expected = (w.executeQuery(query));

        for(String s : expected){
            System.out.println(s);
        }

        }catch (InvalidQueryException e){
            fail();
        }
    }

    @Test
    public void testExecuteQuery3(){

        String query = "get page where category is 'Illinois state senators' asc";
        WikiMediator w = new WikiMediator();

        try{
           for(String s :w.executeQuery(query)){
                System.out.println(s);
            }
        }catch (InvalidQueryException e){
            fail();
        }
    }

    @Test
    public void testExecuteQuery4(){

        String query = "get category where ((author is 'CLCStudent' or title is 'Barack Obama') or title is 'Naomi Klein')";
        WikiMediator w = new WikiMediator();

        try{
        for(String s : (w.executeQuery(query))){
            System.out.println(s);
        }
        }catch (InvalidQueryException e){
            fail();
        }

    }

    @Test
    public void testExecuteQuery5(){

        String query = "get page where ((author is 'AndrewOne' or author is 'Sylas') and (title is 'Barack Obama' or category is 'Trees'))";
        WikiMediator w = new WikiMediator();

        try{
        for(String s : (w.executeQuery(query))){
            System.out.println(s);
        }
        }catch (InvalidQueryException e){
            fail();
        }

    }

    @Test
    public void testExecuteQuery6() {

        String query = "get category where (title is 'Grape' and (title is 'Grape' or title is 'Christmas')) desc";
        WikiMediator w = new WikiMediator();

        try {
            for (String s : (w.executeQuery(query))) {
                System.out.println(s);
            }
        } catch (InvalidQueryException e) {
            fail();
        }
    }

    @Test
    public void testExecuteQuery7() {

        String query = "get category where author is 'Elephant'";
        String query1 = "get category where category is 'Apple'";
        String query2 = "get category where title is 'Mitosis'";
        WikiMediator w = new WikiMediator();

        try {
            for (String s : (w.executeQuery(query))) {
                System.out.println(s);
            }
        } catch (InvalidQueryException e) {
            fail();
        }
        try {
            for (String s : (w.executeQuery(query1))) {
                System.out.println(s);
            }
        } catch (InvalidQueryException e) {
            fail();
        }
        try {
            for (String s : (w.executeQuery(query2))) {
                System.out.println(s);
            }
        } catch (InvalidQueryException e) {
            fail();
        }
    }

    @Test
    public void testExecuteQuery8() {

        String query = "get author where author is 'Button'";
        String query1 = "get author where category is 'Fruit' asc";
        String query2 = "get author where title is 'Jesus'";
        WikiMediator w = new WikiMediator();

        try {
            for (String s : (w.executeQuery(query))) {
                System.out.println(s);
            }
        } catch (InvalidQueryException e) {
            fail();
        }
        try {
            for (String s : (w.executeQuery(query1))) {
                System.out.println(s);
            }
        } catch (InvalidQueryException e) {
            fail();
        }
        try {
            for (String s : (w.executeQuery(query2))) {
                System.out.println(s);
            }
        } catch (InvalidQueryException e) {
            fail();
        }
    }

    @Test
    public void testExecuteQuery9() {

        String query = "get page where author is 'Ypna'";
        String query1 = "get page where (category is 'Fruit' and title is 'Apple')";
        String query2 = "get author where (category is 'Animal' and category is 'Fish')";
        String query3 = "get category where (author is 'Belly' or author is 'Ypna')";
        WikiMediator w = new WikiMediator();

        try {
            for (String s : (w.executeQuery(query))) {
                System.out.println(s);
            }
        } catch (InvalidQueryException e) {
            fail();
        }
        try {
            for (String s : (w.executeQuery(query1))) {
                System.out.println(s);
            }
        } catch (InvalidQueryException e) {
            fail();
        }
        try {
            for (String s : (w.executeQuery(query2))) {
                System.out.println(s);
            }
        } catch (InvalidQueryException e) {
            fail();
        }

        try {
            for (String s : (w.executeQuery(query3))) {
                System.out.println(s);
            }
        } catch (InvalidQueryException e) {
            fail();
        }
    }

    }