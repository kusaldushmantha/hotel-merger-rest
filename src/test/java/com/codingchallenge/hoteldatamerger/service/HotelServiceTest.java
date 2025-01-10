package com.codingchallenge.hoteldatamerger.service;

import com.codingchallenge.hoteldatamerger.cachemanager.CacheManager;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.HotelResultCollector;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.SupplierHotel;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.acme.AcmeHotelResult;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.paperflies.PaperfliesHotelResult;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.patagonia.PatagoniaHotelResult;
import com.codingchallenge.hoteldatamerger.model.HotelResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HotelServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private HotelResultCollector acmeCollector;

    @Mock
    private HotelResultCollector paperfliesCollector;

    @Mock
    private HotelResultCollector patagoniaCollector;

    private HotelService hotelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        List<HotelResultCollector> collectors = new ArrayList<>(List.of(acmeCollector, paperfliesCollector, patagoniaCollector));
        hotelService = new HotelService(collectors, cacheManager);
    }

    @Test
    void testGetHotels_CacheHit() {
        // Arrange
        List<HotelResult> cachedResults = List.of(dummyHotelResult("abcd", 1122));
        when(cacheManager.getFilteredResults(anyList(), anyList())).thenReturn(cachedResults);

        // Act
        PaginatedHotelResponse response = hotelService.getHotels(List.of("abcd"), List.of("1122"), 10, 0);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getHotels().size());
        assertEquals("abcd", response.getHotels().getFirst().getContent().getId());
    }

    @Test
    void testGetHotels_CacheMiss() {
        // Arrange
        List<AcmeHotelResult> acmeResults = getDummyAcmeHotels();
        List<PaperfliesHotelResult> paperfliesResults = getDummyPaperfliesHotels();
        List<PatagoniaHotelResult> patagoniaResults = getDummyPatagoniaHotels();

        when(acmeCollector.getAllHotels()).thenReturn(acmeResults);
        when(paperfliesCollector.getAllHotels()).thenReturn(paperfliesResults);
        when(patagoniaCollector.getAllHotels()).thenReturn(patagoniaResults);

        when(cacheManager.getFilteredResults(anyList(), anyList())).thenReturn(null);

        // Act
        PaginatedHotelResponse response = hotelService.getHotels(new ArrayList<>(), new ArrayList<>(), 10, 0);
        // Assert
        assertNotNull(response);
        assertTrue(response.getHotels().isEmpty());
    }

    @Test
    void testGetHotelById_CacheHit() {
        // Arrange
        List<HotelResult> cachedResults = List.of(dummyHotelResult("abcd", 1234));
        when(cacheManager.getFilteredResults(new ArrayList<>(), new ArrayList<>(List.of("abcd")))).thenReturn(cachedResults);
        // Act
        HotelResult result = hotelService.getHotelById("abcd");
        // Assert
        assertNotNull(result);
        assertEquals("abcd", result.getId());
    }

    @Test
    void testGetHotelById_CacheMiss() {
        // Arrange
        when(cacheManager.getFilteredResults(anyList(), eq(List.of("1")))).thenReturn(null);
        // Act
        HotelResult result = hotelService.getHotelById("1");
        // Assert
        assertNull(result);
    }

    @Test
    void testCollectHotelResultsFromSuppliers() {
        // Arrange
        List<AcmeHotelResult> acmeResults = getDummyAcmeHotels();
        List<PaperfliesHotelResult> paperfliesResults = getDummyPaperfliesHotels();
        List<PatagoniaHotelResult> patagoniaResults = getDummyPatagoniaHotels();

        when(acmeCollector.getAllHotels()).thenReturn(acmeResults);
        when(paperfliesCollector.getAllHotels()).thenReturn(paperfliesResults);
        when(patagoniaCollector.getAllHotels()).thenReturn(patagoniaResults);

        // Act
        Map<String, List<SupplierHotel>> resultsMap = hotelService.collectHotelResultsFromSuppliers();

        // Assert
        assertEquals(3, resultsMap.size()); // Ensure 3 unique hotel IDs are collected

        // Check that each supplier's results are correctly categorized
        assertTrue(resultsMap.containsKey("1"));
        assertTrue(resultsMap.containsKey("2"));
        assertTrue(resultsMap.containsKey("3"));
    }

    private List<PatagoniaHotelResult> getDummyPatagoniaHotels() {
        PatagoniaHotelResult res1 = new PatagoniaHotelResult();
        res1.setID("1");
        res1.setDestination(11);

        PatagoniaHotelResult res2 = new PatagoniaHotelResult();
        res2.setID("2");
        res2.setDestination(22);

        return new ArrayList<>(List.of(res1, res2));
    }

    private List<PaperfliesHotelResult> getDummyPaperfliesHotels() {
        PaperfliesHotelResult res1 = new PaperfliesHotelResult();
        res1.setID("1");
        res1.setDestinationID(11);

        PaperfliesHotelResult res2 = new PaperfliesHotelResult();
        res2.setID("3");
        res2.setDestinationID(33);

        return new ArrayList<>(List.of(res1, res2));
    }

    private List<AcmeHotelResult> getDummyAcmeHotels() {
        AcmeHotelResult res1 = new AcmeHotelResult();
        res1.setID("1");
        res1.setDestinationID("11");

        AcmeHotelResult res2 = new AcmeHotelResult();
        res2.setID("3");
        res2.setDestinationID("33");

        return new ArrayList<>(List.of(res1, res2));
    }

    private HotelResult dummyHotelResult(String hotelID, int destinationID) {
        HotelResult result = new HotelResult();
        result.setId(hotelID);
        result.setDestinationId(destinationID);

        return result;
    }
}
