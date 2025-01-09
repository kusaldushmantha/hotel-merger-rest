package com.codingchallenge.hoteldatamerger.merger;

import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.SupplierHotel;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.acme.AcmeHotelResult;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.paperflies.PaperfliesHotelResult;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.patagonia.PatagoniaHotelResult;
import com.codingchallenge.hoteldatamerger.model.HotelAmenities;
import com.codingchallenge.hoteldatamerger.model.HotelImages;
import com.codingchallenge.hoteldatamerger.model.HotelLocation;

import java.util.List;

public class SimpleHotelAttributeResolver implements HotelAttributeResolver {
    private AcmeHotelResult acmeHotelResult;
    private PatagoniaHotelResult patagoniaHotelResult;
    private PaperfliesHotelResult paperfliesHotelResult;

    public SimpleHotelAttributeResolver(List<SupplierHotel> hotels) {

    }

    @Override
    public String resolveId() {
        return "";
    }

    @Override
    public int resolveDestinationId() {
        return 0;
    }

    @Override
    public String resolveName() {
        return "";
    }

    @Override
    public HotelLocation resolveLocation() {
        return null;
    }

    @Override
    public String resolveDescription() {
        return "";
    }

    @Override
    public HotelAmenities resolveAmenities() {
        return null;
    }

    @Override
    public HotelImages resolveImages() {
        return null;
    }

    @Override
    public List<String> resolveBookingConditions() {
        return List.of();
    }
}
