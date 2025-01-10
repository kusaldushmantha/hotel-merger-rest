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
import com.codingchallenge.hoteldatamerger.model.HotelLocation;
import com.codingchallenge.hoteldatamerger.model.HotelResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public List<HotelResult> getHotels(Set<String> destinationIDs, Set<String> hotelIDs) {
        List<HotelResult> result = this.cacheManager.getFilteredResults(destinationIDs, hotelIDs);
        if (result == null) {
            // cache miss. re-cache in the sync flow for simplicity
            result = getAllMergedHotels().stream()
                    .filter(hotel -> (destinationIDs == null || destinationIDs.isEmpty() || destinationIDs.contains(String.valueOf(hotel.getDestinationId()))) &&
                            (hotelIDs == null || hotelIDs.isEmpty() || hotelIDs.contains(hotel.getId())))
                    .toList();
            this.cacheManager.addFilteredResult(destinationIDs, hotelIDs, result);
        }
        return result;
    }

    // converts supplier specific hotel results to a common format by merging
    private List<HotelResult> getAllMergedHotels() {
        // map to collect hotel ID and all supplier hotels for the same hotel id
        Map<String, List<SupplierHotel>> hotelIDSupplierHotelMap = collectHotelResultsFromSuppliers();
        // map to collect merged locations for destination id
        Map<Integer, HotelLocation> locationsMap = mergeHotelLocations(hotelIDSupplierHotelMap);
        // collect the merged hotel results
        List<HotelResult> resultList = new ArrayList<>();

        // merge hotel based on their id and based on the rule processor
        for (Map.Entry<String, List<SupplierHotel>> entry : hotelIDSupplierHotelMap.entrySet()) {
            try {
                HotelAttributeResolver resolver = new SimpleHotelAttributeResolver(entry.getValue(), locationsMap);
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

    public Map<Integer, HotelLocation> mergeHotelLocations(Map<String, List<SupplierHotel>> supplierHotels) {
        Map<Integer, HotelLocation> result = new HashMap<>();

        for (List<SupplierHotel> hotels : supplierHotels.values()) {
            for (SupplierHotel hotel : hotels) {

                PatagoniaHotelResult patagoniaHotelResult = null;
                AcmeHotelResult acmeHotelResult = null;
                PaperfliesHotelResult paperfliesHotelResult = null;

                // get the destination id.
                int destinationID = -1;
                if (hotel instanceof PaperfliesHotelResult) {
                    paperfliesHotelResult = (PaperfliesHotelResult) hotel;
                    destinationID = paperfliesHotelResult.getDestinationID();
                } else if (hotel instanceof PatagoniaHotelResult) {
                    patagoniaHotelResult = (PatagoniaHotelResult) hotel;
                    destinationID = patagoniaHotelResult.getDestination();
                } else if (hotel instanceof AcmeHotelResult) {
                    acmeHotelResult = (AcmeHotelResult) hotel;
                    if (acmeHotelResult.getDestinationID() == null || acmeHotelResult.getDestinationID().isBlank()) {
                        // invalid entry. skip
                        continue;
                    }
                    destinationID = Integer.parseInt(acmeHotelResult.getDestinationID());
                }

                if (destinationID == -1) {
                    // no hotel contains a valid destination id. no need to proceed further.
                    continue;
                }

                result.putIfAbsent(destinationID, new HotelLocation());
                HotelLocation location = result.get(destinationID);

                // Merge fields based on priority
                // lat/lng can be taken from either of patagonia or acme hotels
                if (patagoniaHotelResult != null) {
                    if (patagoniaHotelResult.getLatitude() != 0) {
                        location.setLat(patagoniaHotelResult.getLatitude());
                    }
                    if (patagoniaHotelResult.getLongitude() != 0) {
                        location.setLng(patagoniaHotelResult.getLongitude());
                    }
                } else if (acmeHotelResult != null) {
                    if (acmeHotelResult.getLatitude() != 0 && location.getLat() == 0) {
                        location.setLat(acmeHotelResult.getLatitude());
                    }
                    if (acmeHotelResult.getLongitude() != 0 && location.getLng() == 0) {
                        location.setLng(acmeHotelResult.getLongitude());
                    }
                }

                // priority for patagonia hotels to get the address
                if (patagoniaHotelResult != null && patagoniaHotelResult.getAddress() != null && !patagoniaHotelResult.getAddress().isBlank()) {
                    location.setAddress(StringUtils.capitalize(patagoniaHotelResult.getAddress()));
                } else if (location.getAddress() == null) {
                    // if patagonia hotels fail, try with paperflies or acme hotels.
                    if (paperfliesHotelResult != null && paperfliesHotelResult.getLocation() != null && paperfliesHotelResult.getLocation().getAddress() != null && !paperfliesHotelResult.getLocation().getAddress().isBlank()) {
                        location.setAddress(StringUtils.capitalize(paperfliesHotelResult.getLocation().getAddress()));
                    } else if (acmeHotelResult != null && acmeHotelResult.getAddress() != null && !acmeHotelResult.getAddress().isBlank()) {
                        location.setAddress(StringUtils.capitalize(acmeHotelResult.getAddress()));
                    }
                }

                // acme hotels are the only ones providing city
                if (acmeHotelResult != null && acmeHotelResult.getCity() != null) {
                    location.setCity(StringUtils.capitalize(acmeHotelResult.getCity()));
                }

                // get the country from paperflies or acme.
                if (paperfliesHotelResult != null && paperfliesHotelResult.getLocation() != null && paperfliesHotelResult.getLocation().getCountry() != null && !paperfliesHotelResult.getLocation().getCountry().isBlank()) {
                    location.setCountry(StringUtils.capitalize(paperfliesHotelResult.getLocation().getCountry()));
                } else if (location.getCountry() == null && acmeHotelResult != null && acmeHotelResult.getCountry() != null && !acmeHotelResult.getCountry().isBlank()) {
                    location.setCountry(StringUtils.capitalize(acmeHotelResult.getCountry()));
                }
            }
        }
        return result;
    }

    private Map<String, List<SupplierHotel>> collectHotelResultsFromSuppliers() {
        // Map to collect hotel results concurrently based on the hotel ID.
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
