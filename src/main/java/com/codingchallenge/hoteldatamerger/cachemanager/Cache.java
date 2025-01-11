package com.codingchallenge.hoteldatamerger.cachemanager;

// cache interface to be used by any cache implementation
public interface Cache<K, V> {
    V get(K key);

    void put(K key, V value);

    void clear();

    void remove(K key);
}
