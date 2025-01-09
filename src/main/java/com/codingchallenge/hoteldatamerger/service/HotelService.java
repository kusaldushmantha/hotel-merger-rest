package com.codingchallenge.hoteldatamerger.service;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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

    public HotelService(List<HotelResultCollector> hotelResultCollectors) {
        this.hotelResultCollectors = hotelResultCollectors;
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE); // limit the number of threads created by using a thread pool
    }

    // converts supplier specific hotel results to a common format by merging
    public List<HotelResult> getAllHotels() {
        Map<String, List<SupplierHotel>> supplierHotelsMap = collectHotelResultsFromSuppliers();

        // merge hotel based on their id and based on the rule processor
        for (Map.Entry<String, List<SupplierHotel>> entry: supplierHotelsMap.entrySet()) {
            HotelAttributeResolver resolver = new SimpleHotelAttributeResolver(entry.getValue());
            HotelResultMergeProcessor processor = new HotelResultMergeProcessor(resolver);

            HotelResult result = processor.mergeDetails();
        }

        return List.of();
    }

    private Map<String, List<SupplierHotel>> collectHotelResultsFromSuppliers() {
        // Lists to collect supplier results
        List<AcmeHotelResult> acmeHotelResults = new ArrayList<>();
        List<PaperfliesHotelResult> paperfliesHotelResults = new ArrayList<>();
        List<PatagoniaHotelResult> patagoniaHotelResults = new ArrayList<>();

        // Map to collect hotel results based on the hotel ID.
        Map<String, List<SupplierHotel>> resultsMap = new HashMap<>();

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
            }, this.threadPool).thenAccept(responseBody -> {
                if (responseBody != null && !responseBody.isEmpty()) {
                    Object result = responseBody.getFirst(); // check the type of the first result

                    // collect the results based on result type
                    switch (result) {
                        case AcmeHotelResult _ -> acmeHotelResults.addAll(responseBody);
                        case PaperfliesHotelResult _ -> paperfliesHotelResults.addAll(responseBody);
                        case PatagoniaHotelResult _ -> patagoniaHotelResults.addAll(responseBody);
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

        // Add AcmeHotelResults to the map
        for (AcmeHotelResult result : acmeHotelResults) {
            resultsMap.computeIfAbsent(result.getID(), _ -> new ArrayList<>()).add(result);
        }

        // Add PaperfliesHotelResults to the map
        for (PaperfliesHotelResult result : paperfliesHotelResults) {
            resultsMap.computeIfAbsent(result.getID(), _ -> new ArrayList<>()).add(result);
        }

        // Add PatagoniaHotelResults to the map
        for (PatagoniaHotelResult result : patagoniaHotelResults) {
            resultsMap.computeIfAbsent(result.getID(), _ -> new ArrayList<>()).add(result);
        }

        return resultsMap;
    }
}
