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
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class SimpleCache<K, V> implements Cache<K, V> {

    private final Map<K, V> cache;
    private final int maxSize; // to limit the entries in the cache
    private final Random random;
    private final ReentrantReadWriteLock lock;

    public SimpleCache(@Value("${cache.max-size}") int maxSize, @Value("${cache.ttl.mins}") int clearIntervalMinutes) {
        this.cache = new ConcurrentHashMap<>();
        this.maxSize = maxSize;
        this.random = new Random();
        this.lock = new ReentrantReadWriteLock();

        // Schedule a task to clear the cache every `clearIntervalMinutes`
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            lock.writeLock().lock();
            try {
                clear();
                System.out.println("Cache cleared!");
            } finally {
                lock.writeLock().unlock();
            }
        }, clearIntervalMinutes, clearIntervalMinutes, TimeUnit.MINUTES);
    }

    // Add a new key-value pair to the cache
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            if (cache.size() >= maxSize) {
                replaceRandomEntry();
            }
            cache.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

    @Override
    public void remove(K key) {
        lock.writeLock().lock();
        try {
            this.cache.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Retrieve a value from the cache
    public V get(K key) {
        lock.readLock().lock();
        try {
            return cache.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    // Replace a random entry in the cache
    private void replaceRandomEntry() {
        lock.writeLock().lock();
        try {
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
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Size of the cache
    public int size() {
        lock.readLock().lock();
        try {
            return cache.size();
        } finally {
            lock.readLock().unlock();
        }
    }
}
