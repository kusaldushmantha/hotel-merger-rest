package com.codingchallenge.hoteldatamerger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HotelImages {
    @JsonProperty("rooms")
    private List<HotelImage> roomImages;

    @JsonProperty("site")
    private List<HotelImage> siteImages;

    @JsonProperty("amenities")
    private List<HotelImage> amenitiesImages;
}
