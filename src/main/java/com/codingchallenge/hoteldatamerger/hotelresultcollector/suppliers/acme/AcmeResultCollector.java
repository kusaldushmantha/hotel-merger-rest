package com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.acme;

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
public class AcmeResultCollector implements HotelResultCollector<AcmeHotelResult> {
    private static final Logger LOGGER = Logger.getLogger(AcmeResultCollector.class.getName());

    private final HTTPClient httpClient;
    private final String getHotelsURL;

    public AcmeResultCollector(
            @Value("${suppliers.acme.gethotels.v1}") String getHotelsURL,
            @Value("${suppliers.acme.timeout}") Duration timeout) {

        this.getHotelsURL = getHotelsURL;
        this.httpClient = new HTTPClient(timeout);
    }

    @Override
    public List<AcmeHotelResult> getAllHotels() {
        try {
            HttpResponse<String> response = this.httpClient.doGet(getHotelsURL);
            // Parse the response body into a List
            ObjectMapper objectMapper = new ObjectMapper();
            List<AcmeHotelResult> results = objectMapper.readValue(response.body(), new TypeReference<>() {
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
