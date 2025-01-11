package com.codingchallenge.hoteldatamerger.cachemanager;

import com.codingchallenge.hoteldatamerger.model.HotelResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CacheManager {
    private static final Logger LOGGER = Logger.getLogger(CacheManager.class.getName());

    private final Cache<String, List<HotelResult>> cache;

    public CacheManager(Cache<String, List<HotelResult>> cache) {
        this.cache = cache;
    }

    // Get filtered results with caching
    public List<HotelResult> getFilteredResults(List<String> destinationIDs, List<String> hotelIDs) {
        String cacheKey = buildCacheKey(destinationIDs, hotelIDs);
        if (cache.get(cacheKey) != null) {
            LOGGER.log(Level.INFO, "Cache hit. Key: " + cacheKey);
            return cache.get(cacheKey);
        }
        LOGGER.log(Level.INFO, "Cache miss. Key: " + cacheKey);
        return null;
    }

    // Add results to cache
    public void addFilteredResult(List<String> destinationIDs, List<String> hotelIDs, List<HotelResult> result) {
        String cacheKey = buildCacheKey(destinationIDs, hotelIDs);
        this.cache.put(cacheKey, result);
        LOGGER.log(Level.INFO, "Added to cache. Key: " + cacheKey);
    }

    // Build the cache key based on destinationIDs and hotelIDs
    String buildCacheKey(List<String> destinationIDs, List<String> hotelIDs) {
        String destinationKey = (destinationIDs == null || destinationIDs.isEmpty()) ? "all" : String.join("_", destinationIDs);
        String hotelKey = (hotelIDs == null || hotelIDs.isEmpty()) ? "all" : String.join("_", hotelIDs);
        return "DEST:::" + destinationKey + "|HTL:::" + hotelKey;
    }


}
