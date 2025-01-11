package com.codingchallenge.hoteldatamerger.cachemanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SimpleCache<K, V> implements Cache<K, V> {
    private static final Logger LOGGER = Logger.getLogger(SimpleCache.class.getName());

    private final Map<K, V> cache;
    private final int maxSize; // to limit the entries in the cache
    private final Random random;

    public SimpleCache(@Value("${cache.max-size}") int maxSize, @Value("${cache.ttl.mins}") int clearIntervalMinutes) {
        this.cache = new ConcurrentHashMap<>(); // use concurrent hash map to ensure thread safety
        this.maxSize = maxSize;
        this.random = new Random();

        // Schedule a task to clear the cache every `clearIntervalMinutes`
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        LOGGER.log(Level.INFO, "Simple cache started. auto evict all keys every " + clearIntervalMinutes + " mins");
        scheduler.scheduleAtFixedRate(() -> {
            clear();
            LOGGER.log(Level.INFO, "Simple cache cleared");
        }, clearIntervalMinutes, clearIntervalMinutes, TimeUnit.MINUTES);
    }

    // Add a new key-value pair to the cache
    public void put(K key, V value) {
        if (cache.size() >= maxSize) {
            replaceRandomEntry();
        }
        cache.put(key, value);
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

    @Override
    public void remove(K key) {
        this.cache.remove(key);
    }

    // Retrieve a value from the cache
    public V get(K key) {
        return cache.get(key);
    }

    // Replace a random entry in the cache
    private void replaceRandomEntry() {
        if (cache.isEmpty()) return;
        Iterator<K> iterator = cache.keySet().iterator();
        int randomIndex = random.nextInt(cache.size());
        for (int i = 0; i <= randomIndex; i++) {
            if (i == randomIndex && iterator.hasNext()) {
                K randomKey = iterator.next();
                cache.remove(randomKey);
            } else {
                iterator.next();
            }
        }
    }

    // Size of the cache
    public int size() {
        return cache.size();
    }
}
