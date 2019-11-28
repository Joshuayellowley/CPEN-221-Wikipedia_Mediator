package cpen221.mp3.cache;

import java.time.Instant;
import java.time.Duration;
import java.util.*;


public class Cache<T extends Cacheable> {

    /* the default cache size is 32 objects */
    public static final int DSIZE = 32;

    /* the default timeout value is 3600s */
    public static final int DTIMEOUT = 3600;

    private int capacity;
    private int timeout;
    private HashMap<T,Instant> storage;

    /* TODO: Implement this datatype */

    /**
     * Create a cache with a fixed capacity and a timeout value.
     * Objects in the cache that have not been refreshed within the timeout period
     * are removed from the cache.
     *
     * @param capacity the number of objects the cache can hold
     * @param timeout the duration, in seconds, an object should be in the cache before it times out
     */
    public Cache(int capacity, int timeout) {
        // TODO: help
        this.capacity = capacity;
        this.timeout = timeout;
        this.storage = new HashMap<T, Instant>();
    }


    /**
     * Create a cache with default capacity and timeout values.
     */
    public Cache() {
        this(DSIZE, DTIMEOUT);
    }

    /**
     * Add a value to the cache.
     * If the cache is full then remove the least recently accessed object to
     * make room for the new object.
     */
    boolean put(T t) {
        if(storage.size() < this.capacity) {
            storage.put(t, Instant.now());
            return true;
        }else{
            for(T b : storage.keySet()){
                if(b.id().equals(t.id())){
                    return false;
                }
            }

            Set<T> storageSet = storage.keySet();
            long min = 0;
            //TODO helP idk what do here
            T toRemove = null;
            for(T q : storageSet){
                Instant i = storage.get(q);
                long timeElapsed = Duration.between(i, Instant.now()).toMillis();
                if(timeElapsed > min){
                    min = timeElapsed;
                    toRemove = q;
                }
            }
            storage.remove(toRemove);
            storage.put(t,Instant.now());
        }
        //TODO What ze fuck is this
        return true;
    }

    /**
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the cache
     */
    T get(String id) {
        /* TODO: Is this allowed */

        for(T t : storage.keySet()){
            if(t.id().equals(id)){
                return t;
            }
        }

         /* Do not return null. Throw a suitable checked exception when an object
            is not in the cache. */

        throw new NoSuchElementException();
    }

    /**
     * Update the last refresh time for the object with the provided id.
     * This method is used to mark an object as "not stale" so that its timeout
     * is delayed.
     *
     * @param id the identifier of the object to "touch"
     * @return true if successful and false otherwise
     */
    boolean touch(String id) {
        /* TODO: CHeck it TreV */

        for(T t : storage.keySet()){
            if(t.id().equals(id)){
                storage.replace(t,Instant.now());
                return true;
            }
        }

        return false;
    }

    /**
     * Update an object in the cache.
     * This method updates an object and acts like a "touch" to renew the
     * object in the cache.
     *
     * @param t the object to update
     * @return true if successful and false otherwise
     */
    boolean update(T t) {
        /* TODO: Help TREBBB */

        for(T v : storage.keySet()){
            if(t.id().equals(v.id())){
                storage.remove(v);
                storage.put(t,Instant.now());
                return true;
            }
        }

        return false;
    }

}
