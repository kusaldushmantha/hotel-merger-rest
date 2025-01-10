package com.codingchallenge.hoteldatamerger.merger;

import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.SupplierHotel;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.acme.AcmeHotelResult;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.paperflies.PaperfliesHotelImage;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.paperflies.PaperfliesHotelResult;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.patagonia.PatagoniaHotelImage;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.patagonia.PatagoniaHotelResult;
import com.codingchallenge.hoteldatamerger.model.HotelAmenities;
import com.codingchallenge.hoteldatamerger.model.HotelImage;
import com.codingchallenge.hoteldatamerger.model.HotelImages;
import com.codingchallenge.hoteldatamerger.model.HotelLocation;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * SimpleHotelAttributeResolver represents a simple merge logic based on attributes from different
 * suppliers
 * */
public class SimpleHotelAttributeResolver implements HotelAttributeResolver {
    private static final Logger LOGGER = Logger.getLogger(SimpleHotelAttributeResolver.class.getName());

    private AcmeHotelResult acmeHotelResult;
    private PatagoniaHotelResult patagoniaHotelResult;
    private PaperfliesHotelResult paperfliesHotelResult;

    private final Map<Integer, HotelLocation> locationMap;
    private final List<SupplierHotel> supplierHotels;

    public SimpleHotelAttributeResolver(List<SupplierHotel> hotels, Map<Integer, HotelLocation> locationsMap) {
        this.locationMap = locationsMap;
        this.supplierHotels = hotels;
        this.resolveTypes();
    }

    public void resolveTypes() throws IllegalArgumentException {
        // this list contains only one hotel per supplier
        for (SupplierHotel hotel : this.supplierHotels) {
            switch (hotel) {
                case AcmeHotelResult h -> acmeHotelResult = h;
                case PaperfliesHotelResult h -> paperfliesHotelResult = h;
                case PatagoniaHotelResult h -> patagoniaHotelResult = h;
                case null, default -> {
                    LOGGER.log(Level.WARNING, "unknown response object");
                }
            }
        }

        // if the supplier hotel does not belong to any of the valid suppliers. return an exception.
        if (acmeHotelResult == null && paperfliesHotelResult == null && patagoniaHotelResult == null) {
            throw new IllegalArgumentException("cannot resolve hotels");
        }
    }

    @Override
    public String resolveId() {
        // at least one of the hotel result must be not-null
        if (acmeHotelResult != null) {
            return acmeHotelResult.getID();
        }
        if (patagoniaHotelResult != null) {
            return patagoniaHotelResult.getID();
        }
        if (paperfliesHotelResult != null) {
            return paperfliesHotelResult.getID();
        }
        return "";
    }

    @Override
    public int resolveDestinationId() {
        // at least one of the hotel result must be not-null
        if (acmeHotelResult != null && acmeHotelResult.getDestinationID() != null && !acmeHotelResult.getDestinationID().isBlank()) {
            try {
                return Integer.parseInt(acmeHotelResult.getDestinationID().strip());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "error while parsing destination id from acme hotel. trying another supplier", e);
            }
        }
        if (patagoniaHotelResult != null) {
            return patagoniaHotelResult.getDestination();
        }
        if (paperfliesHotelResult != null) {
            return paperfliesHotelResult.getDestinationID();
        }
        return -1;
    }

    @Override
    public String resolveName() {
        // at least one of the hotel result must be not-null
        if (acmeHotelResult != null && acmeHotelResult.getName() != null && !acmeHotelResult.getName().isBlank()) {
            return acmeHotelResult.getName().strip();
        }
        if (patagoniaHotelResult != null && patagoniaHotelResult.getName() != null && !patagoniaHotelResult.getName().isBlank()) {
            return patagoniaHotelResult.getName().strip();
        }
        if (paperfliesHotelResult != null && paperfliesHotelResult.getName() != null && !paperfliesHotelResult.getName().isBlank()) {
            return paperfliesHotelResult.getName().strip();
        }
        return "";
    }

    @Override
    public HotelLocation resolveLocation() {
        return this.locationMap.get(resolveDestinationId());
    }

    @Override
    public String resolveDescription() {
        // description with the highest details will be returned
        String description = "";
        if (paperfliesHotelResult != null && paperfliesHotelResult.getDetails() != null && !paperfliesHotelResult.getDetails().isBlank() && description.length() < paperfliesHotelResult.getDetails().strip().length()) {
            description = paperfliesHotelResult.getDetails().strip();
        }
        if (patagoniaHotelResult != null && patagoniaHotelResult.getInfo() != null && !patagoniaHotelResult.getInfo().isBlank() && description.length() < patagoniaHotelResult.getInfo().strip().length()) {
            description = patagoniaHotelResult.getInfo().strip();
        }
        if (acmeHotelResult != null && acmeHotelResult.getDescription() != null && !acmeHotelResult.getDescription().isBlank() && description.length() < acmeHotelResult.getDescription().strip().length()) {
            description = acmeHotelResult.getDescription().strip();
        }
        return StringUtils.capitalize(description);
    }

    @Override
    public HotelAmenities resolveAmenities() {
        HotelAmenities amenities = new HotelAmenities();
        amenities.setGeneral(new ArrayList<>());
        amenities.setRoom(new ArrayList<>());

        Set<String> generalAmenities = new HashSet<>();
        Set<String> roomAmenities = new HashSet<>();

        // paperflies hotels have separate general and room amenities. give first priority to it
        if (paperfliesHotelResult != null && paperfliesHotelResult.getAmenities() != null) {
            if (paperfliesHotelResult.getAmenities().getGeneral() != null) {
                for (String ga : paperfliesHotelResult.getAmenities().getGeneral()) {
                    String key = getKey(ga); // deduplicate entries based on a generated key
                    if (!generalAmenities.contains(key)) {
                        generalAmenities.add(key);
                        amenities.getGeneral().add(StringUtils.capitalize(ga.strip()));
                    }
                }
            }
            if (paperfliesHotelResult.getAmenities().getRoom() != null) {
                for (String ra : paperfliesHotelResult.getAmenities().getRoom()) {
                    String key = getKey(ra); // deduplicate entries based on a generated key
                    if (!roomAmenities.contains(key)) {
                        roomAmenities.add(key);
                        amenities.getRoom().add(StringUtils.capitalize(ra.strip()));
                    }
                }
            }
        }
        // collect amenities from patagonia hotels and add to room amenities as they align best to room amenities.
        // If the amenity is already present as a general amenity, do not add to room amenity again.
        if (patagoniaHotelResult != null && patagoniaHotelResult.getAmenities() != null) {
            for (String ga : patagoniaHotelResult.getAmenities()) {
                String key = getKey(ga);
                if (!generalAmenities.contains(key) && !roomAmenities.add(key)) {
                    roomAmenities.add(key);
                    amenities.getRoom().add(StringUtils.capitalize(ga.strip()));
                }
            }
        }
        // collect amenities from acme hotels and add to general amenities as they align best with general amenities.
        // If the amenity is already present as a room amenity, do not add to general amenity again.
        if (acmeHotelResult != null && acmeHotelResult.getFacilities() != null) {
            for (String ga : acmeHotelResult.getFacilities()) {
                String key = getKey(ga);
                if (!generalAmenities.contains(key) && !roomAmenities.add(key)) {
                    generalAmenities.add(key);
                    amenities.getGeneral().add(StringUtils.capitalize(ga.strip()));
                }
            }
        }

        return amenities;
    }

    @Override
    public HotelImages resolveImages() {
        HotelImages images = new HotelImages();

        Set<HotelImage> siteImages = new HashSet<>();
        Set<HotelImage> amenitiesImages = new HashSet<>();
        Set<HotelImage> roomImages = new HashSet<>();

        // first priority paperflies as these hotels contain comprehensive image breakdown
        if (paperfliesHotelResult != null && paperfliesHotelResult.getImages() != null) {
            // extract site images available in paperflies hotels
            if (paperfliesHotelResult.getImages().getSite() != null) {
                for (PaperfliesHotelImage img : paperfliesHotelResult.getImages().getSite()) {
                    if (img == null || img.getCaption() == null || img.getLink() == null || img.getCaption().isBlank() || img.getLink().isBlank()) {
                        // dirty data. ignore
                        continue;
                    }
                    siteImages.add(new HotelImage(img.getLink().strip(), StringUtils.capitalize(img.getCaption().strip())));
                }
            }
            // extract room images and room types
            if (paperfliesHotelResult.getImages().getRooms() != null) {
                for (PaperfliesHotelImage img : paperfliesHotelResult.getImages().getRooms()) {
                    if (img == null || img.getCaption() == null || img.getLink() == null || img.getCaption().isBlank() || img.getLink().isBlank()) {
                        // dirty data. ignore
                        continue;
                    }
                    roomImages.add(new HotelImage(img.getLink().strip(), StringUtils.capitalize(img.getCaption().strip())));
                }
            }
        }

        if (patagoniaHotelResult != null && patagoniaHotelResult.getImages() != null) {
            // extract amenities images from paperflies hotels
            if (patagoniaHotelResult.getImages().getAmenities() != null) {
                for (PatagoniaHotelImage img : patagoniaHotelResult.getImages().getAmenities()) {
                    if (img == null || img.getDescription() == null || img.getUrl() == null || img.getDescription().isBlank() || img.getUrl().isBlank()) {
                        // dirty data. ignore
                        continue;
                    }
                    amenitiesImages.add(new HotelImage(img.getUrl().strip(), StringUtils.capitalize(img.getDescription().strip())));
                }
            }
            // extract room images and room types
            if (patagoniaHotelResult.getImages().getRooms() != null) {
                for (PatagoniaHotelImage img : patagoniaHotelResult.getImages().getRooms()) {
                    if (img == null || img.getDescription() == null || img.getUrl() == null || img.getDescription().isBlank() || img.getUrl().isBlank()) {
                        // dirty data. ignore
                        continue;
                    }
                    roomImages.add(new HotelImage(img.getUrl().strip(), StringUtils.capitalize(img.getDescription().strip())));
                }
            }
        }

        images.setSiteImages(new ArrayList<>(siteImages));
        images.setAmenitiesImages(new ArrayList<>(amenitiesImages));
        images.setRoomImages(new ArrayList<>(roomImages));

        return images;
    }

    @Override
    public List<String> resolveBookingConditions() {
        // only paperflies hotels have booking conditions
        List<String> bookingConditions = new ArrayList<>();
        if (paperfliesHotelResult != null && paperfliesHotelResult.getBookingConditions() != null && !paperfliesHotelResult.getBookingConditions().isEmpty()) {
            for (String condition : paperfliesHotelResult.getBookingConditions()) {
                bookingConditions.add(StringUtils.capitalize(condition));
            }
        }
        return bookingConditions;
    }


    // This is used to deduplicate entries
    private static String getKey(String ga) {
        StringBuilder sb = new StringBuilder();
        for (char c : ga.strip().toCharArray()) {
            if (Character.isAlphabetic(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
