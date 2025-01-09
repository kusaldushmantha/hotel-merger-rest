package com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.patagonia;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PatagoniaHotelImages {
    private List<PatagoniaHotelImage> rooms;
    private List<PatagoniaHotelImage> amenities;
}
