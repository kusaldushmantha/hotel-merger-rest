package com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.paperflies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
 * PaperfliesHotelResult is the entity representing the JSON response from Patagonia supplier
 */
@Getter
@Setter
public class PaperfliesHotelResult {
    @JsonProperty("hotel_id")
    private String ID;

    @JsonProperty("destination_id")
    private int destinationID;

    @JsonProperty("hotel_name")
    private String name;

    @JsonProperty("location")
    private PaperfliesHotelLocation location;

    @JsonProperty("details")
    private String details;

    @JsonProperty("amenities")
    private PaperfliesHotelAmenities amenities;

    @JsonProperty("images")
    private PaperfliesHotelImages images;

    @JsonProperty("booking_conditions")
    private List<String> bookingConditions;
}
