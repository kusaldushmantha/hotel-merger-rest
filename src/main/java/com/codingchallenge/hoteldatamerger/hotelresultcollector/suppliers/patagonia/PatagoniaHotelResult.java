package com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.patagonia;

import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.SupplierHotel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
 * PatagoniaHotelResult is the entity representing the JSON response from Patagonia supplier
 */
@Getter
@Setter
public class PatagoniaHotelResult implements SupplierHotel {
    @JsonProperty("id")
    private String ID;

    @JsonProperty("destination")
    private int destination;

    @JsonProperty("name")
    private String name;

    @JsonProperty("lat")
    private float latitude;

    @JsonProperty("lng")
    private float longitude;

    @JsonProperty("address")
    private String address;

    @JsonProperty("info")
    private String info;

    @JsonProperty("amenities")
    private List<String> amenities;

    @JsonProperty("images")
    private PatagoniaHotelImages images;
}
