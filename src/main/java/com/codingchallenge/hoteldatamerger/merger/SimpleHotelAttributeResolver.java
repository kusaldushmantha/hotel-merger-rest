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
import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleHotelAttributeResolver implements HotelAttributeResolver {
    private static final Logger LOGGER = Logger.getLogger(SimpleHotelAttributeResolver.class.getName());

    private AcmeHotelResult acmeHotelResult;
    private PatagoniaHotelResult patagoniaHotelResult;
    private PaperfliesHotelResult paperfliesHotelResult;

    public SimpleHotelAttributeResolver(List<SupplierHotel> hotels) {
        // this list contains only one hotel per supplier
        for (SupplierHotel hotel : hotels) {
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
        // at least one of the hotel result must be not-null
        if (acmeHotelResult != null) {
            return acmeHotelResult.getID();
        } else if (patagoniaHotelResult != null) {
            return patagoniaHotelResult.getID();
        }
        return paperfliesHotelResult.getID();
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
        return paperfliesHotelResult.getDestinationID();
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
        HotelLocation location = new HotelLocation();

        // first priority should be given to patagonia hotels as their APIs provides the most complete location info
        if (patagoniaHotelResult != null) {
            if (patagoniaHotelResult.getLatitude() == 0) {
                location.setLat(patagoniaHotelResult.getLatitude());
            }
            if (patagoniaHotelResult.getLongitude() == 0) {
                location.setLng(patagoniaHotelResult.getLongitude());
            }
            if (patagoniaHotelResult.getAddress() != null && !patagoniaHotelResult.getAddress().isBlank()) {
                location.setAddress(patagoniaHotelResult.getAddress().strip());
            }
        }
        // second priority to paperflies hotels to fill address and country
        if (paperfliesHotelResult != null && paperfliesHotelResult.getLocation() != null) {
            if (paperfliesHotelResult.getLocation().getCountry() != null && !paperfliesHotelResult.getLocation().getCountry().isBlank()) {
                location.setCountry(WordUtils.capitalizeFully(paperfliesHotelResult.getLocation().getCountry().strip()));
            }
            if ((location.getAddress() == null || location.getAddress().isBlank()) && (paperfliesHotelResult.getLocation().getAddress() != null && !paperfliesHotelResult.getLocation().getAddress().isBlank())) {
                location.setAddress(paperfliesHotelResult.getLocation().getAddress().strip());
            }
        }
        // fill remaining unfilled fields with acme hotels
        if (acmeHotelResult != null) {
            // only acme hotels provide a city
            if (acmeHotelResult.getCity() != null && !acmeHotelResult.getCity().isBlank()) {
                location.setCity(WordUtils.capitalizeFully(acmeHotelResult.getCity().strip()));
            }
            if (location.getCountry() == null || location.getCountry().isBlank()) {
                location.setCountry(WordUtils.capitalizeFully(acmeHotelResult.getCountry().strip()));
            }
            if (location.getLat() == 0) {
                location.setLat(acmeHotelResult.getLatitude());
            }
            if (location.getLng() == 0) {
                location.setLat(acmeHotelResult.getLongitude());
            }
        }

        return location;
    }

    @Override
    public String resolveDescription() {
        // description with the highest details comes first
        if (paperfliesHotelResult != null && paperfliesHotelResult.getDetails() != null && !paperfliesHotelResult.getDetails().isBlank()) {
            return paperfliesHotelResult.getDetails().strip();
        }
        if (patagoniaHotelResult != null && patagoniaHotelResult.getInfo() != null && !patagoniaHotelResult.getInfo().isBlank()) {
            return patagoniaHotelResult.getInfo().strip();
        }
        if (acmeHotelResult != null && acmeHotelResult.getDescription() != null && !acmeHotelResult.getDescription().isBlank()) {
            return acmeHotelResult.getDescription().strip();
        }
        return "";
    }

    @Override
    public HotelAmenities resolveAmenities() {
        HotelAmenities amenities = new HotelAmenities();
        amenities.setGeneral(new ArrayList<>());
        amenities.setRoom(new ArrayList<>());

        Set<String> generalAmenities = new HashSet<>();
        Set<String> roomAmenities = new HashSet<>();

        // paperflies hotels have separate general and room amenities
        if (paperfliesHotelResult != null && paperfliesHotelResult.getAmenities() != null) {
            if (paperfliesHotelResult.getAmenities().getGeneral() != null) {
                for (String ga : paperfliesHotelResult.getAmenities().getGeneral()) {
                    String key = getKey(ga);
                    if (!generalAmenities.contains(key)) {
                        generalAmenities.add(key);
                        amenities.getGeneral().add(WordUtils.capitalizeFully(ga.strip()));
                    }
                }
            }
            if (paperfliesHotelResult.getAmenities().getRoom() != null) {
                for (String ra : paperfliesHotelResult.getAmenities().getRoom()) {
                    String key = getKey(ra);
                    if (!roomAmenities.contains(key)) {
                        roomAmenities.add(key);
                        amenities.getRoom().add(WordUtils.capitalizeFully(ra.strip()));
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
                    amenities.getRoom().add(WordUtils.capitalizeFully(ga.strip()));
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
                    amenities.getGeneral().add(WordUtils.capitalizeFully(ga.strip()));
                }
            }
        }

        return amenities;
    }

    private static String getKey(String ga) {
        StringBuilder sb = new StringBuilder();
        for (char c : ga.strip().toCharArray()) {
            if (Character.isAlphabetic(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public HotelImages resolveImages() {
        HotelImages images = new HotelImages();

        List<HotelImage> siteImages = new ArrayList<>();
        List<HotelImage> amenitiesImages = new ArrayList<>();
        Set<HotelImage> roomImages = new HashSet<>();


        if (paperfliesHotelResult != null && paperfliesHotelResult.getImages() != null) {
            // extract site images available in paperflies hotels
            if (paperfliesHotelResult.getImages().getSite() != null) {
                for (PaperfliesHotelImage img : paperfliesHotelResult.getImages().getSite()) {
                    if (img == null || img.getCaption() == null || img.getLink() == null || img.getCaption().isBlank() || img.getLink().isBlank()) {
                        // dirty data. ignore
                        continue;
                    }
                    siteImages.add(new HotelImage(img.getLink().strip(), WordUtils.capitalizeFully(img.getCaption().strip())));
                }
            }
            // extract room images and room types
            if (paperfliesHotelResult.getImages().getRooms() != null) {
                for (PaperfliesHotelImage img : paperfliesHotelResult.getImages().getRooms()) {
                    if (img == null || img.getCaption() == null || img.getLink() == null || img.getCaption().isBlank() || img.getLink().isBlank()) {
                        // dirty data. ignore
                        continue;
                    }
                    roomImages.add(new HotelImage(img.getLink().strip(), WordUtils.capitalizeFully(img.getCaption().strip())));
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
                    amenitiesImages.add(new HotelImage(img.getUrl().strip(), WordUtils.capitalizeFully(img.getDescription().strip())));
                }
            }
            // extract room images and room types
            if (patagoniaHotelResult.getImages().getRooms() != null) {
                for (PatagoniaHotelImage img : patagoniaHotelResult.getImages().getRooms()) {
                    if (img == null || img.getDescription() == null || img.getUrl() == null || img.getDescription().isBlank() || img.getUrl().isBlank()) {
                        // dirty data. ignore
                        continue;
                    }
                    roomImages.add(new HotelImage(img.getUrl().strip(), WordUtils.capitalizeFully(img.getDescription().strip())));
                }
            }
        }

        images.setSiteImages(siteImages);
        images.setAmenitiesImages(amenitiesImages);
        images.setRoomImages(new ArrayList<>(roomImages));

        return images;
    }

    @Override
    public List<String> resolveBookingConditions() {
        // only paperflies hotels have booking conditions
        if (paperfliesHotelResult != null && !paperfliesHotelResult.getBookingConditions().isEmpty()) {
            return paperfliesHotelResult.getBookingConditions();
        }
        return new ArrayList<>();
    }
}
