package cpen221.mp3.cache;

import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Representation Invariant:
 * capacity > 0
 * timeout > 0
 * storage not null
 * lastTimeOpened not null
 * storage.size() == lastTimeOpened.size()
 * storage.contains(T) == lastTimeOpened.contains(T)
 * storage.get(T) <= Instant.now()
 * lastTimeOpened.get(T) <= Instant.now()
 * <p>
 * <p>
 * Abstraction Function:
 * Represents a cache that stores
 * capacity generic objects 'T', in storage and lastTimeOpened
 * for timeout seconds.
 * Every T in storage is mapped to the time it was put into the cache.
 * Every T in lastTimeOpened is mapped to the last time it was accessed/touched
 * Any object that has been in the cache for timeout seconds is removed
 * next time cache is used/updated.
 * Default capacity is 32 Objects, and default timeout is 12 hours,
 * 43200 seconds.
 * <p>
 * <p>
 * This data type is mutable.
 */
public class Cache<T extends Cacheable> {

    /* the default cache size is 32 objects */
    public static final int DSIZE = 32;

    /* the default timeout value is 12h */
    //THIS IS IN SECONDS
    public static final int DTIMEOUT = 60 * 60 * 12;

    private int capacity;
    private int timeout;
    private HashMap<T, Instant> storage;
    private HashMap<T, Instant> lastTimeOpened;

    /**
     * Create a cache with a fixed capacity and a timeout value.
     * If capacity and timeout are non-positive, create a cache with default values
     * Objects in the cache that have not been refreshed within the timeout period
     * are removed from the cache upon next update/use.
     *
     * @param capacity the number of objects the cache can hold,
     *                 if non-positive, gets DSIZE
     * @param timeout  the duration, in seconds, an object should
     *                 be in the cache before it times out.
     *                 If non-positive, gets DTIMEOUT
     */
    public Cache(int capacity, int timeout) {
        this.capacity = DSIZE;
        this.timeout = DTIMEOUT;
        if (capacity > 0) {
            this.capacity = capacity;
        }
        if (timeout > 0) {
            this.timeout = timeout;
        }
        this.storage = new HashMap<>();
        this.lastTimeOpened = new HashMap<>();
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
     * make room for the new object.  If the id of t is an id already contained
     * by the Cache, or t is null return false.
     *
     * @param t of type T which extends Cacheable
     * @return true if the object was successfully added to the Cache and false otherwise
     * @mutates this.storage
     */
    public boolean put(T t) {

        clearOldEntries();

        if (t == null) {
            return false;
        }

        if (storage.size() < this.capacity) {
            storage.put(t, Instant.now());
            lastTimeOpened.put(t, Instant.now());
            return true;
        } else {
            for (T b : storage.keySet()) {
                if (b.id().equals(t.id())) {
                    return false;
                }
            }

            long min = Long.MIN_VALUE;
            T toRemove = null;
            if (lastTimeOpened.size() != 0) {
                for (T q : lastTimeOpened.keySet()) {
                    Instant i = lastTimeOpened.get(q);
                    long timeElapsed = Duration.between(i, Instant.now()).toNanos();
                    System.out.println(timeElapsed);
                    if (timeElapsed > min) {
                        min = timeElapsed;
                        toRemove = q;
                    }
                }
                storage.remove(toRemove);
                lastTimeOpened.remove(toRemove);
                storage.put(t, Instant.now());
                lastTimeOpened.put(t, Instant.now());
            }
//
        }
        return true;
    }

    /**
     * Finds the given object T, within the cache with the given id.
     *
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the cache, if no object
     * is found throws a NoSuchElementException
     * @throws NoSuchElementException If no object with the given id is found in the cache
     */
    public T get(String id) {
        clearOldEntries();

        for (T t : storage.keySet()) {
            if (t.id().equals(id)) {
                lastTimeOpened.replace(t, Instant.now());
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
     * If the id is not contained in the cache returns false
     * @mutates this.storage
     */
    public boolean touch(String id) {

        clearOldEntries();

        if (id == null) {
            return false;
        }

        for (T t : storage.keySet()) {
            if (t.id().equals(id)) {
                storage.replace(t, Instant.now());
                return true;
            }
        }

        return false;
    }

    /**
     * Updates an item in the cache.
     * This method replaces an old object with a new object with the same id.
     * Updates the timeout value, marking it as "not stale."
     *
     * @param t the object to update
     * @return true if successful and the object has been modified and false otherwise
     * @mutates this.storage
     */

    public boolean update(T t) {

        clearOldEntries();

        for (T v : storage.keySet()) {
            if (t.id().equals(v.id())) {
                this.storage.replace(t, Instant.now());
                return true;
            }
        }
        return false;
    }

    /**
     * Clears entry that have been in the cache for a time period longer than
     * the specified timeout.
     */
    private void clearOldEntries() {

        List<T> toRemove = new ArrayList<>();
        for (T t : storage.keySet()) {
            if (Duration.between(storage.get(t), Instant.now()).toNanos() / 1000000
                    >= this.timeout) {
                toRemove.add(t);
            }
        }

        for (T t : toRemove) {
            storage.remove(t);
            lastTimeOpened.remove(t);
        }

    }

}
