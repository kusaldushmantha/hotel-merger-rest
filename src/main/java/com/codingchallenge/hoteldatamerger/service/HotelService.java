package com.codingchallenge.hoteldatamerger.service;

import com.codingchallenge.hoteldatamerger.cachemanager.CacheManager;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.HotelResultCollector;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.SupplierHotel;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.acme.AcmeHotelResult;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.paperflies.PaperfliesHotelResult;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.patagonia.PatagoniaHotelResult;
import com.codingchallenge.hoteldatamerger.merger.HotelAttributeResolver;
import com.codingchallenge.hoteldatamerger.merger.HotelResultMergeProcessor;
import com.codingchallenge.hoteldatamerger.merger.SimpleHotelAttributeResolver;
import com.codingchallenge.hoteldatamerger.model.HotelResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class HotelService {
    private static final Logger LOGGER = Logger.getLogger(HotelService.class.getName());
    private static final int THREAD_POOL_SIZE = 5;

    private final List<HotelResultCollector> hotelResultCollectors;
    private final ExecutorService threadPool;
    // read-through cache to cache results
    private final CacheManager cacheManager;

    public HotelService(List<HotelResultCollector> hotelResultCollectors, CacheManager cacheManager) {
        this.hotelResultCollectors = hotelResultCollectors;
        this.cacheManager = cacheManager;
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE); // limit the number of threads created by using a thread pool
    }

    public PaginatedHotelResponse getHotels(List<String> destinationIDs, List<String> hotelIDs, int limit, int offset) {
        List<HotelResult> result = this.cacheManager.getFilteredResults(destinationIDs, hotelIDs);
        if (result == null) {
            // cache miss. re-cache in the sync flow for simplicity
            result = getAllMergedHotels().stream()
                    .filter(hotel -> (destinationIDs == null || destinationIDs.isEmpty() || destinationIDs.contains(String.valueOf(hotel.getDestinationId()))) &&
                            (hotelIDs == null || hotelIDs.isEmpty() || hotelIDs.contains(hotel.getId())))
                    .toList();
            this.cacheManager.addFilteredResult(destinationIDs, hotelIDs, result);
        }

        // Paginate the result manually using subList
        int start = Math.min(offset, result.size());
        int end = Math.min(start + limit, result.size());
        List<HotelResult> paginatedResults = result.subList(start, end);

        // Use custom pagination for simplicity
        PaginatedHotelResponse response = new PaginatedHotelResponse(paginatedResults, result.size(), limit, offset);

        int totalCount = result.size();
        if (end < totalCount) {
            response.addNextLink(limit, offset, destinationIDs, hotelIDs);
        }
        if (start > 0) {
            response.addPrevLink(limit, offset, destinationIDs, hotelIDs);
        }

        return response;
    }

    public HotelResult getHotelById(String hotelID) {
        List<String> hotelIDs = List.of(hotelID);
        List<HotelResult> result = this.cacheManager.getFilteredResults(new ArrayList<>(), hotelIDs);
        if (result == null) {
            // cache miss. re-cache in the sync flow for simplicity
            result = getAllMergedHotels().stream()
                    .filter(hotel -> hotelIDs.contains(hotel.getId()))
                    .toList();
            this.cacheManager.addFilteredResult(new ArrayList<>(), hotelIDs, result);
        }
        if (result.isEmpty()) {
            // no hotel with the provided id
            return null;
        }
        return result.getFirst();
    }

    // converts supplier specific hotel results to a common format by merging
    private List<HotelResult> getAllMergedHotels() {
        // map to collect hotel ID and all supplier hotels for the same hotel id
        Map<String, List<SupplierHotel>> hotelIDSupplierHotelMap = collectHotelResultsFromSuppliers();
        // collect the merged hotel results
        List<HotelResult> resultList = new ArrayList<>();

        // merge hotel based on their id and based on the rule processor
        for (Map.Entry<String, List<SupplierHotel>> entry : hotelIDSupplierHotelMap.entrySet()) {
            // iteration happens on the same hotel id but with different providers. i.e. hotel_id and destination_id same for all hotels
            try {
                HotelAttributeResolver resolver = new SimpleHotelAttributeResolver(entry.getValue());
                HotelResultMergeProcessor processor = new HotelResultMergeProcessor(resolver);

                HotelResult result = processor.mergeDetails();
                if (result.getName().isBlank() || result.getId().isBlank() || result.getDestinationId() == -1) {
                    // invalid result. skip
                    continue;
                }
                resultList.add(result);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "error occurred while resolving attributes", e);
            }
        }

        return resultList;
    }

    // collect hotels from different suppliers
    Map<String, List<SupplierHotel>> collectHotelResultsFromSuppliers() {
        // Map to collect hotel results parallel based on the hotel ID.
        Map<String, List<SupplierHotel>> resultsMap = new ConcurrentHashMap<>();

        // Execute requests in parallel
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (HotelResultCollector resultCollector : this.hotelResultCollectors) {
            // create futures to execute in parallel
            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return resultCollector.getAllHotels();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "error occurred while querying supplier", e);
                }
                return null;
            }, this.threadPool).thenAccept(responseHotelsList -> {
                if (responseHotelsList != null && !responseHotelsList.isEmpty()) {
                    Object result = responseHotelsList.getFirst(); // check the type of the first result

                    // collect the results based on result type
                    switch (result) {
                        case AcmeHotelResult _ -> {
                            // Add AcmeHotelResults to the map
                            for (Object hotelResult : responseHotelsList) {
                                AcmeHotelResult acmeHotelResult = (AcmeHotelResult) hotelResult;
                                resultsMap.computeIfAbsent(acmeHotelResult.getID(), _ -> new ArrayList<>()).add(acmeHotelResult);
                            }
                        }
                        case PaperfliesHotelResult _ -> {
                            // Add PaperfliesHotelResults to the map
                            for (Object hotelResult : responseHotelsList) {
                                PaperfliesHotelResult paperfliesHotelResult = (PaperfliesHotelResult) hotelResult;
                                resultsMap.computeIfAbsent(paperfliesHotelResult.getID(), _ -> new ArrayList<>()).add(paperfliesHotelResult);
                            }
                        }
                        case PatagoniaHotelResult _ -> {
                            // Add PatagoniaHotelResults to the map
                            for (Object hotelResult : responseHotelsList) {
                                PatagoniaHotelResult patagoniaHotelResult = (PatagoniaHotelResult) hotelResult;
                                resultsMap.computeIfAbsent(patagoniaHotelResult.getID(), _ -> new ArrayList<>()).add(patagoniaHotelResult);
                            }
                        }
                        case null, default -> {
                            LOGGER.log(Level.WARNING, "unknown response object");
                        }
                    }
                }
            });

            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return resultsMap;
    }
}
