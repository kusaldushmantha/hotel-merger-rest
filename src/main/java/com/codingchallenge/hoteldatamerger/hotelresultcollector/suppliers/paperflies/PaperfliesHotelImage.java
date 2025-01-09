package com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.paperflies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaperfliesHotelImage {
    @JsonProperty("link")
    private String link;

    @JsonProperty("caption")
    private String caption;
}
