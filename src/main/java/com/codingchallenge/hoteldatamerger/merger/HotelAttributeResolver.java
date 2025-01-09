package com.codingchallenge.hoteldatamerger.merger;

import com.codingchallenge.hoteldatamerger.model.HotelAmenities;
import com.codingchallenge.hoteldatamerger.model.HotelImages;
import com.codingchallenge.hoteldatamerger.model.HotelLocation;

import java.util.List;

public interface HotelAttributeResolver {
    // returns the resolved hotel ID
    String resolveId();

    // returns the resolved hotel destination ID
    int resolveDestinationId();

    // returns the resolved hotel name
    String resolveName();

    // returns the resolved hotel location
    HotelLocation resolveLocation();

    // returns the resolved hotel description
    String resolveDescription();

    // returns the resolved hotel amenities
    HotelAmenities resolveAmenities();

    // returns the resolved hotel images
    HotelImages resolveImages();

    // returns the resolved booking conditions
    List<String> resolveBookingConditions();
}
