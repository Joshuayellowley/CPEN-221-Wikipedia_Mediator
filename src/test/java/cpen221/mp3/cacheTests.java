package cpen221.mp3;

import cpen221.mp3.cache.Cache;
import cpen221.mp3.cache.Cacheable;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class cacheTests <T extends Cacheable> {

    @Test
    public void putIntoCache(){
        Cache specificCache = new Cache(2, 20);
        ToCache t1 = new ToCache("1");
        ToCache t2 = new ToCache("2");
        ToCache t3 = null;
        ToCache t4 = new ToCache("4");

        assertTrue(specificCache.put(t1));
        assertTrue(specificCache.put(t2));
        assertFalse(specificCache.put(t3));
        assertTrue(specificCache.put(t4));
        assertFalse(specificCache.put(t2));
        assertFalse (specificCache.put(t4));
    }

    @Test
    public void putIntoCache2(){
        Cache specificCache = new Cache(2, 20);
        ToCache t1 = new ToCache("1");
        ToCache t2 = new ToCache("2");
        ToCache t3 = null;
        ToCache t4 = new ToCache("4");

        assertTrue(specificCache.put(t1));
        assertTrue(specificCache.put(t2));
        assertFalse(specificCache.put(t3));
        specificCache.get(t2.id());
        specificCache.get(t1.id());
        assertFalse(specificCache.put(t2));
        assertTrue(specificCache.put(t4));
        specificCache.put(t1);
    }

    @Test
    public void putIntoCache3(){
        Cache specificCache = new Cache(2, 2000);
        ToCache t1 = new ToCache("1");
        ToCache t2 = new ToCache("2");
        ToCache t3 = new ToCache("3");
        ToCache t4 = new ToCache("4");

        assertTrue(specificCache.put(t1));
        assertTrue(specificCache.put(t2));
        assertTrue(specificCache.put(t3));
        assertTrue(specificCache.put(t4));
        assertFalse(specificCache.update(t3));
        assertTrue(specificCache.update(t4));
        assertFalse(specificCache.update(t1));
        assertTrue(specificCache.put(t1));
        assertFalse(specificCache.update(t4));
        assertTrue(specificCache.update(t1));
    }

    @Test
    public void touchInCache() {
        Cache specificCache = new Cache(2, 20);
        ToCache t1 = new ToCache("1");
        ToCache t2 = new ToCache("2");
        ToCache t3 = null;
        ToCache t4 = new ToCache("4");

        assertTrue(specificCache.put(t1));
        assertTrue(specificCache.put(t2));
        assertTrue(specificCache.touch(t1.id()));
        assertFalse(specificCache.touch(t4.id()));
        assertFalse(specificCache.touch(null));
        assertTrue(specificCache.put(t4));
        assertTrue(specificCache.touch(t2.id()));
    }

    @Test
    public void updateInCache() {
        Cache defaultCache = new Cache();

        ToCache t1 = new ToCache("1");
        ToCache t2 = new ToCache("2");
        ToCache t3 = null;
        ToCache t4 = new ToCache("4");

        assertTrue(defaultCache.put(t1));
        assertTrue(defaultCache.put(t2));
        assertTrue(defaultCache.update(t1));
        assertFalse(defaultCache.update(t4));
        assertTrue(defaultCache.update(t2));
    }

    @Test
    public void clearOldEntries() {
        Cache sCache = new Cache(32, 1);

        ToCache t1 = new ToCache("1");
        ToCache t2 = new ToCache("2");
        ToCache t3 = null;
        ToCache t4 = new ToCache("4");

        assertTrue(sCache.put(t1));
        assertTrue(sCache.put(t2));
        try
        {
            Thread.sleep(1500);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }

        try {
            sCache.get(t1.id());
            fail();
        }catch(NoSuchElementException e){

        }

    }



    private class ToCache implements Cacheable {

        String id;

        public ToCache(String id) {
            this.id = id;
        }

        public String id(){return this.id;}
    }
    }