package com.codingchallenge.hoteldatamerger.hotelresultcollector;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HTTPClient {

    private final Duration timeout;

    public HTTPClient() {
        this.timeout = Duration.ofSeconds(5); // Default timeout set to 5 sec.
    }

    public HTTPClient(Duration timeout) {
        this.timeout = timeout;
    }

    public HttpResponse<String> doGet(String URL) throws IOException, InterruptedException, HttpServerErrorException {
        // create the http client
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest
                    .newBuilder().uri(URI.create(URL))
                    .GET()
                    .timeout(this.timeout)
                    .build();

            // Send the request and get the response as a String
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // downstream server error.
            if(response.statusCode() != HttpStatus.OK.value()) {
                throw new HttpServerErrorException(HttpStatus.valueOf(response.statusCode()), "server error");
            }
            return response;
        }
    }
}
