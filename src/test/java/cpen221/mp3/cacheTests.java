package cpen221.mp3;

import cpen221.mp3.cache.Cache;
import cpen221.mp3.cache.Cacheable;
import org.junit.Test;

import static org.junit.Assert.*;

public class cacheTests <T extends Cacheable> {

    private Cache defaultCache = new Cache();
    private Cache specificCache = new Cache(2, 20);

    @Test
    public void putIntoCache(){
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
    public void touchInCache() {
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







    private class ToCache implements Cacheable{

        String id;

        public ToCache(String id){
            this.id = id;
        }

        public String id(){ return this.id;}

    }

}
