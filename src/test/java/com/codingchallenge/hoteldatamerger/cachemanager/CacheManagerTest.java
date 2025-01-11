package com.codingchallenge.hoteldatamerger.cachemanager;

import com.codingchallenge.hoteldatamerger.model.HotelResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CacheManagerTest {

    @Mock
    private SimpleCache<String, List<HotelResult>> cache;

    @InjectMocks
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFilteredResultsCacheHit() {
        // Arrange
        List<String> destinationIDs = List.of("Dest1");
        List<String> hotelIDs = List.of("Hotel1");
        String cacheKey = cacheManager.buildCacheKey(destinationIDs, hotelIDs);
        List<HotelResult> cachedResult = new ArrayList<>();
        cachedResult.add(new HotelResult()); // Mocking a HotelResult

        when(cache.get(cacheKey)).thenReturn(cachedResult);

        // Act
        List<HotelResult> result = cacheManager.getFilteredResults(destinationIDs, hotelIDs);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredResultsCacheMiss() {
        // Arrange
        List<String> destinationIDs = List.of("Dest1");
        List<String> hotelIDs = List.of("Hotel1");
        String cacheKey = cacheManager.buildCacheKey(destinationIDs, hotelIDs);
        when(cache.get(cacheKey)).thenReturn(null);  // Simulate cache miss
        // Act
        List<HotelResult> result = cacheManager.getFilteredResults(destinationIDs, hotelIDs);
        // Assert
        assertNull(result);
    }

    @Test
    void testAddFilteredResult() {
        // Arrange
        List<String> destinationIDs = List.of("Dest1");
        List<String> hotelIDs = List.of("Hotel1");
        String cacheKey = cacheManager.buildCacheKey(destinationIDs, hotelIDs);
        List<HotelResult> result = new ArrayList<>();
        result.add(new HotelResult());  // Mocking a HotelResult

        // Act
        cacheManager.addFilteredResult(destinationIDs, hotelIDs, result);

        // Assert
        verify(cache, times(1)).put(cacheKey, result);
    }

    @Test
    void testBuildCacheKey() {
        List<String> destinationIDs = List.of("Dest1", "Dest2");
        List<String> hotelIDs = List.of("Hotel1");

        String cacheKey = cacheManager.buildCacheKey(destinationIDs, hotelIDs);

        assertEquals("DEST:::Dest1_Dest2|HTL:::Hotel1", cacheKey);
    }
}

