package com.codingchallenge.hoteldatamerger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelLocation {
    @JsonProperty("lat")
    private float lat;

    @JsonProperty("lng")
    private float lng;

    @JsonProperty("address")
    private String address;

    @JsonProperty("city")
    private String city;

    @JsonProperty("country")
    private String country;
}
