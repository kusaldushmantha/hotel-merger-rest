package com.codingchallenge.hoteldatamerger.cachemanager;

// simple cache interface
public interface Cache<K, V> {
    V get(K key);

    void put(K key, V value);

    void clear();

    void remove(K key);
}
