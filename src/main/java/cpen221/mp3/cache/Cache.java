package cpen221.mp3.cache;

import java.time.Instant;
import java.time.Duration;
import java.util.*;


public class Cache<T extends Cacheable> {

    /* the default cache size is 32 objects */
    public static final int DSIZE = 256;

    /* the default timeout value is 12h */
    //THIS IS IN MILLISECONDS
    public static final int DTIMEOUT = 1000*60*60*12;

    private int capacity;
    private int timeout;
    private HashMap<T,Instant> storage;
    private HashMap<T,Instant> lastTimeOpened;
    //private Thread t = new Thread(new CacheThread());

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
        // TODO: help, METHODS ARE ALL PUBLIC IS THAT OK
        this.capacity = capacity;
        this.timeout = timeout;
        this.storage = new HashMap<T, Instant>();
        //this.t.start();
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
     * make room for the new object.  If the id of the instance T is an id already contained
     * by the Cache, return false.
     *
     * @param t of type T which extends Cacheable
     * @return true if the object was successfully added to the Cache and false otherwise
     */
    public boolean put(T t) {

        clearOldEntries();

        if(t == null){
            return false;
        }

        if(storage.size() < this.capacity) {
            storage.put(t, Instant.now());
            return true;
        }else{
            for(T b : storage.keySet()){
                if(b.id().equals(t.id())){
                    return false;
                }
            }

            long min = 0;
            //TODO helP idk what do here
            T toRemove = null;
            for(T q : lastTimeOpened.keySet()){
                Instant i = lastTimeOpened.get(q);
                long timeElapsed = Duration.between(i, Instant.now()).toMillis();
                if(timeElapsed > min){
                    min = timeElapsed;
                    toRemove = q;
                }
            }
            storage.remove(toRemove);
            lastTimeOpened.remove(toRemove);
            storage.put(t,Instant.now());
            lastTimeOpened.put(t,Instant.now());

        }
        //TODO What the heck is this
        return true;
    }

    /**
     * Finds the given object T, within the cache with the given id.
     *
     * @throws NoSuchElementException //TODO fix this
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the cache, if no object
     * is found throws a NoSuchElementException
     */
    public T get(String id) {
        clearOldEntries();

        for(T t : storage.keySet()){
            if(t.id().equals(id)){
                lastTimeOpened.replace(t,Instant.now());
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
     * is delayed.  Updates the last accessed time of the object to the current time.
     *
     * @param id the identifier of the object to "touch"
     * @return true if the id is contained in the cache and the last accessed time is changed.
     *         If the id is not contained in the cache returns false
     *
     */
    public boolean touch(String id) {

        clearOldEntries();

        for(T t : storage.keySet()){
            if(t.id().equals(id)){
                storage.replace(t,Instant.now());
                return true;
            }
        }

        return false;
    }

    /**
     * Updates an item in the cache.  This does not influence the staleness
     * or last time the item was accessed.
     *
     * @param t the object to update
     * @return true if successful and the object has been modified and false otherwise
     */
    public boolean update(T t) {
        /* TODO: Help TREBBB */

        clearOldEntries();

        for(T v : storage.keySet()){
            if(t.id().equals(v.id())){
                storage.remove(v);
                storage.replace(t,Instant.now());
                return true;
            }
        }

        return false;
    }


    /**
     * Clears entry that have been in the cache for a time period longer than
     * the specified timeout.
     */
    private void clearOldEntries(){

        for(T t : storage.keySet()){
            if(Duration.between(storage.get(t),Instant.now()).toMillis() >= this.timeout){
                storage.remove(t);
            }
        }
    }
//     class CacheThread implements Runnable{
//
//        public void run(){
//            while(true) {
//                for (T t : storage.keySet()) {
//                    if (Duration.between(storage.get(t), Instant.now()).toMillis() >= DTIMEOUT) {
//                        storage.remove(t);
//                    }
//                }
//            }
//        }
//
//    }

}
