package com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.patagonia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatagoniaHotelImage {
    @JsonProperty("url")
    private String url;

    @JsonProperty("description")
    private String description;
}
