package com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.patagonia;

import com.codingchallenge.hoteldatamerger.hotelresultcollector.HTTPClient;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.HotelResultCollector;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class PatagoniaHotelResultCollector implements HotelResultCollector<PatagoniaHotelResult> {
    private static final Logger LOGGER = Logger.getLogger(PatagoniaHotelResultCollector.class.getName());

    private final HTTPClient httpClient;
    private final String getHotelsURL;

    public PatagoniaHotelResultCollector(
            @Value("${suppliers.patagonia.gethotels.v1}") String getHotelsURL,
            @Value("${suppliers.patagonia.timeout}") Duration timeout) {

        this.getHotelsURL = getHotelsURL;
        this.httpClient = new HTTPClient(timeout);
    }

    @Override
    public List<PatagoniaHotelResult> getAllHotels() {
        try {
            HttpResponse<String> response = this.httpClient.doGet(getHotelsURL);
            // Parse the response body into a List
            ObjectMapper objectMapper = new ObjectMapper();
            List<PatagoniaHotelResult> results = objectMapper.readValue(response.body(), new TypeReference<>() {
            });

            return results;

        } catch (HttpServerErrorException e) {
            // downstream server error.
            LOGGER.log(Level.WARNING, "server returned an error", e);
        } catch (IOException e) {
            // an error happened.
            LOGGER.log(Level.SEVERE, "error while collecting hotel results", e);
        } catch (InterruptedException e) {
            // a timeout happened.
            LOGGER.log(Level.SEVERE, "request timeout while collecting hotel results", e);
        }
        return new ArrayList<>();
    }
}
