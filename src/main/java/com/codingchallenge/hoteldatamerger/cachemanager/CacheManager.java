package com.codingchallenge.hoteldatamerger.cachemanager;

import com.codingchallenge.hoteldatamerger.model.HotelResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class CacheManager {
    private final SimpleCache<String, List<HotelResult>> cache;

    public CacheManager(SimpleCache<String, List<HotelResult>> cache) {
        this.cache = cache;
    }

    // Get filtered results with caching
    public List<HotelResult> getFilteredResults(Set<String> destinationIDs, Set<String> hotelIDs) {
        String cacheKey = buildCacheKey(destinationIDs, hotelIDs);
        if (cache.get(cacheKey) != null) {
            return cache.get(cacheKey);
        }
        return null;
    }

    // Add results to cache
    public void addFilteredResult(Set<String> destinationIDs, Set<String> hotelIDs, List<HotelResult> result) {
        String cacheKey = buildCacheKey(destinationIDs, hotelIDs);
        this.cache.put(cacheKey, result);
    }

    // Build the cache key based on destinationIDs and hotelIDs
    private String buildCacheKey(Set<String> destinationIDs, Set<String> hotelIDs) {
        String destinationKey = (destinationIDs == null || destinationIDs.isEmpty()) ? "all" : String.join("_", destinationIDs.toString());
        String hotelKey = (hotelIDs == null || hotelIDs.isEmpty()) ? "all" : String.join("_", hotelIDs.toString());
        return "DEST:::" + destinationKey + "|HTL:::" + hotelKey;
    }


}
