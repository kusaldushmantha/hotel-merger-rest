package com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.paperflies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaperfliesHotelAmenities {
    @JsonProperty("general")
    private List<String> general;

    @JsonProperty("room")
    private List<String> room;
}
