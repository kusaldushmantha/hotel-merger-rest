package com.codingchallenge.hoteldatamerger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelImage {
    @JsonProperty("link")
    private String link;

    @JsonProperty("description")
    private String description;
}
