package com.codingchallenge.hoteldatamerger.merger;

import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.SupplierHotel;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.acme.AcmeHotelResult;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.paperflies.PaperfliesHotelResult;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.patagonia.PatagoniaHotelResult;
import com.codingchallenge.hoteldatamerger.model.HotelAmenities;
import com.codingchallenge.hoteldatamerger.model.HotelImages;
import com.codingchallenge.hoteldatamerger.model.HotelLocation;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleHotelAttributeResolver implements HotelAttributeResolver {
    private static final Logger LOGGER = Logger.getLogger(SimpleHotelAttributeResolver.class.getName());

    private AcmeHotelResult acmeHotelResult;
    private PatagoniaHotelResult patagoniaHotelResult;
    private PaperfliesHotelResult paperfliesHotelResult;

    public SimpleHotelAttributeResolver(List<SupplierHotel> hotels) {
        // this list contains only one hotel per supplier
        for(SupplierHotel hotel: hotels) {
            switch (hotel) {
                case AcmeHotelResult h -> acmeHotelResult = h;
                case PaperfliesHotelResult h -> paperfliesHotelResult = h;
                case PatagoniaHotelResult h -> patagoniaHotelResult = h;
                case null, default -> {
                    LOGGER.log(Level.WARNING, "unknown response object");
                }
            }
        }
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
