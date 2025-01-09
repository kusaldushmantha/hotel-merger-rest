package com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.patagonia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PatagoniaHotelImages {
    @JsonProperty("rooms")
    private List<PatagoniaHotelImage> rooms;

    @JsonProperty("amenities")
    private List<PatagoniaHotelImage> amenities;
}
