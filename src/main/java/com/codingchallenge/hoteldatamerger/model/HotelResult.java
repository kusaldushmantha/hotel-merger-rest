package com.codingchallenge.hoteldatamerger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HotelResult {
    @JsonProperty("id")
    private String Id;

    @JsonProperty("destination_id")
    private int destinationId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("location")
    private HotelLocation location;

    @JsonProperty("description")
    private String description;

    @JsonProperty("amenities")
    private HotelAmenities amenities;

    @JsonProperty("images")
    private HotelImages images;

    @JsonProperty("booking_conditions")
    private List<String> bookingConditions;
}
