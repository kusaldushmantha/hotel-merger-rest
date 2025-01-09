package com.codingchallenge.hoteldatamerger.merger;

import com.codingchallenge.hoteldatamerger.model.HotelResult;

public class HotelResultMergeProcessor {

    private final HotelAttributeResolver attributeResolver;

    public HotelResultMergeProcessor(HotelAttributeResolver attributeResolver) {
        this.attributeResolver = attributeResolver;
    }

    public HotelResult mergeDetails() {
        HotelResult result = new HotelResult();

        result.setId(this.attributeResolver.resolveId());
        result.setName(this.attributeResolver.resolveName());
        result.setAmenities(this.attributeResolver.resolveAmenities());
        result.setDescription(this.attributeResolver.resolveDescription());
        result.setImages(this.attributeResolver.resolveImages());
        result.setLocation(this.attributeResolver.resolveLocation());
        result.setBookingConditions(this.attributeResolver.resolveBookingConditions());
        result.setDestinationId(this.attributeResolver.resolveDestinationId());

        return result;
    }
}
