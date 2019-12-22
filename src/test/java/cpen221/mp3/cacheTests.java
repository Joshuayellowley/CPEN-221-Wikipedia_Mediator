package cpen221.mp3;

import cpen221.mp3.cache.Cache;
import cpen221.mp3.cache.Cacheable;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class cacheTests <T extends Cacheable> {

//    private Cache defaultCache = new Cache();
//    private Cache specificCache = new Cache(2, 20);
//    private Cache sCache = new Cache(32, 1);

    @Test
    public void putIntoCache(){
        Cache cache1 = new Cache(2, 600);
        ToCache t1 = new ToCache("1");
        ToCache t2 = new ToCache("2");
        ToCache t3 = null;
        ToCache t4 = new ToCache("4");

        assertTrue(cache1.put(t1));
        assertTrue(cache1.put(t2));
        assertFalse(cache1.put(t3));
        cache1.put(t1);
        assertFalse(cache1.put(t2));
        assertTrue(cache1.put(t4));
    }

    @Test
    public void putIntoCache2(){
        Cache cache2 = new Cache(2, 600);
        ToCache t1 = new ToCache("1");
        ToCache t2 = new ToCache("2");
        ToCache t3 = null;
        ToCache t4 = new ToCache("4");

        assertTrue(cache2.put(t1));
        assertTrue(cache2.put(t2));
        assertFalse(cache2.put(t3));
        cache2.get(t2.id());
        cache2.get(t1.id());
        assertFalse(cache2.put(t2));
        assertTrue(cache2.put(t4));
        cache2.put(t1);
    }

    @Test
    public void putIntoCache3(){
        Cache cache3 = new Cache(2, 6000);
        ToCache t1 = new ToCache("1");
        ToCache t2 = new ToCache("2");
        ToCache t3 = new ToCache("3");
        ToCache t4 = new ToCache("4");

        assertTrue(cache3.put(t1));
        assertTrue(cache3.put(t2));
        assertTrue(cache3.put(t3));
        assertTrue(cache3.put(t4));
        assertTrue(cache3.update(t3));
        assertTrue(cache3.update(t4));
        assertFalse(cache3.update(t1));
        assertTrue(cache3.put(t1));
        assertTrue(cache3.update(t1));
    }

    @Test
    public void touchInCache() {
        Cache specificCache = new Cache(2, 600);
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
        specificCache.touch(t2.id());
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