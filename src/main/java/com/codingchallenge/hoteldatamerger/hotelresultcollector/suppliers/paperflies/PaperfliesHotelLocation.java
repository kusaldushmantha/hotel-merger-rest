package com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.paperflies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaperfliesHotelLocation {
    @JsonProperty("address")
    private String address;

    @JsonProperty("country")
    private String country;
}
