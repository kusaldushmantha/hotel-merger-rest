package com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.paperflies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaperfliesHotelImages {
    @JsonProperty("rooms")
    private List<PaperfliesHotelImage> rooms;

    @JsonProperty("site")
    private List<PaperfliesHotelImage> site;
}