package com.codingchallenge.hoteldatamerger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HotelAmenities {
    @JsonProperty("general")
    private List<String> general;

    @JsonProperty("room")
    private List<String> room;
}
