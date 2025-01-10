package com.codingchallenge.hoteldatamerger.cachemanager;

import com.codingchallenge.hoteldatamerger.model.HotelResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CacheManager {
    private static final Logger LOGGER = Logger.getLogger(CacheManager.class.getName());

    private final SimpleCache<String, List<HotelResult>> cache;

    public CacheManager(SimpleCache<String, List<HotelResult>> cache) {
        this.cache = cache;
    }

    // Get filtered results with caching
    public List<HotelResult> getFilteredResults(List<String> destinationIDs, List<String> hotelIDs) {
        String cacheKey = buildCacheKey(destinationIDs, hotelIDs);
        if (cache.get(cacheKey) != null) {
            LOGGER.log(Level.INFO, new StringBuilder("Cache hit. Key: ").append(cacheKey).toString());
            return cache.get(cacheKey);
        }
        LOGGER.log(Level.INFO, new StringBuilder("Cache miss. Key: ").append(cacheKey).toString());
        return null;
    }

    // Add results to cache
    public void addFilteredResult(List<String> destinationIDs, List<String> hotelIDs, List<HotelResult> result) {
        String cacheKey = buildCacheKey(destinationIDs, hotelIDs);
        this.cache.put(cacheKey, result);
        LOGGER.log(Level.INFO, new StringBuilder("Added to cache. Key: ").append(cacheKey).toString());
    }

    // Build the cache key based on destinationIDs and hotelIDs
    String buildCacheKey(List<String> destinationIDs, List<String> hotelIDs) {
        String destinationKey = (destinationIDs == null || destinationIDs.isEmpty()) ? "all" : String.join("_", destinationIDs.toString());
        String hotelKey = (hotelIDs == null || hotelIDs.isEmpty()) ? "all" : String.join("_", hotelIDs.toString());
        return "DEST:::" + destinationKey + "|HTL:::" + hotelKey;
    }


}
