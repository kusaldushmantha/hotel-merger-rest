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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * SimpleHotelAttributeResolver represents a simple merge logic based on attributes from different
 * suppliers
 * */
public class SimpleHotelAttributeResolver implements HotelAttributeResolver {
    private static final Logger LOGGER = Logger.getLogger(SimpleHotelAttributeResolver.class.getName());

    private final List<SupplierHotel> supplierHotels;

    public SimpleHotelAttributeResolver(List<SupplierHotel> hotels) {
        this.supplierHotels = hotels;
    }

    @Override
    public String resolveId() {
        // define the priority order to consider when resolving for id
        List<Class<?>> priorityOrder = List.of(PatagoniaHotelResult.class, PaperfliesHotelResult.class, AcmeHotelResult.class);

        for (Class<?> prioritizedType : priorityOrder) {
            for (SupplierHotel hotel : this.supplierHotels) {
                if (!prioritizedType.isInstance(hotel)) {
                    continue;
                }
                switch (hotel) {
                    case AcmeHotelResult result -> {
                        String id = result.getID();
                        if (id != null && !id.isBlank()) {
                            return id.strip();
                        }
                    }
                    case PatagoniaHotelResult hotelResult -> {
                        String id = hotelResult.getID();
                        if (id != null && !id.isBlank()) {
                            return id.strip();
                        }
                    }
                    case PaperfliesHotelResult hotelResult -> {
                        String id = hotelResult.getID();
                        if (id != null && !id.isBlank()) {
                            return id.strip();
                        }
                    }
                    default -> LOGGER.log(Level.WARNING, "unidentified class instance");
                }
            }
        }
        return "";
    }

    @Override
    public int resolveDestinationId() {
        // define the priority order to consider when resolving for destination id
        List<Class<?>> priorityOrder = List.of(AcmeHotelResult.class, PatagoniaHotelResult.class, PaperfliesHotelResult.class);

        for (Class<?> prioritizedType : priorityOrder) {
            for (SupplierHotel hotel : this.supplierHotels) {
                if (!prioritizedType.isInstance(hotel)) {
                    continue;
                }
                switch (hotel) {
                    case AcmeHotelResult result -> {
                        String destinationID = result.getDestinationID();
                        if (destinationID != null && !destinationID.isBlank()) {
                            return Integer.parseInt(destinationID);
                        }
                    }
                    case PatagoniaHotelResult hotelResult -> {
                        int destinationID = hotelResult.getDestination();
                        if (destinationID != 0) {
                            return destinationID;
                        }
                    }
                    case PaperfliesHotelResult hotelResult -> {
                        int destinationID = hotelResult.getDestinationID();
                        if (destinationID != 0) {
                            return destinationID;
                        }
                    }
                    default -> LOGGER.log(Level.WARNING, "unidentified class instance");
                }

            }
        }
        return -1;
    }

    @Override
    public String resolveName() {
        // define the priority order to consider when resolving name
        List<Class<?>> priorityOrder = List.of(AcmeHotelResult.class, PatagoniaHotelResult.class, PaperfliesHotelResult.class);

        for (Class<?> prioritizedType : priorityOrder) {
            for (SupplierHotel hotel : this.supplierHotels) {
                if (!prioritizedType.isInstance(hotel)) {
                    continue;
                }
                switch (hotel) {
                    case AcmeHotelResult result -> {
                        String name = result.getName();
                        if (name != null && !name.isBlank()) {
                            return name.strip();
                        }
                    }
                    case PatagoniaHotelResult hotelResult -> {
                        String name = hotelResult.getName();
                        if (name != null && !name.isBlank()) {
                            return name.strip();
                        }
                    }
                    case PaperfliesHotelResult hotelResult -> {
                        String name = hotelResult.getName();
                        if (name != null && !name.isBlank()) {
                            return name.strip();
                        }
                    }
                    default -> LOGGER.log(Level.WARNING, "unidentified class instance");
                }
            }
        }
        return "";
    }

    @Override
    public HotelLocation resolveLocation() {
        HotelLocation location = new HotelLocation();

        // define the priority order to consider when resolving for location
        List<Class<?>> priorityOrder = List.of(PatagoniaHotelResult.class, PaperfliesHotelResult.class, AcmeHotelResult.class);

        for (Class<?> prioritizedType : priorityOrder) {
            for (SupplierHotel hotel : this.supplierHotels) {
                if (!prioritizedType.isInstance(hotel)) {
                    continue;
                }
                switch (hotel) {
                    case AcmeHotelResult acmeHotelResult -> {
                        // only set the lat and lng if they are not already set by other high priority suppliers
                        if (location.getLat() == 0 && acmeHotelResult.getLatitude() != 0) {
                            location.setLat(acmeHotelResult.getLatitude());
                        }
                        if (location.getLng() == 0 && acmeHotelResult.getLongitude() != 0) {
                            location.setLng(acmeHotelResult.getLongitude());
                        }
                        if (location.getAddress() == null && acmeHotelResult.getAddress() != null && !acmeHotelResult.getAddress().isBlank()) {
                            // if address is not set. try with acme hotels.
                            location.setAddress(StringUtils.capitalize(acmeHotelResult.getAddress()));
                        }
                        if (acmeHotelResult.getCity() != null) {
                            location.setCity(StringUtils.capitalize(acmeHotelResult.getCity()));
                        }
                        // only set the country if this is not already set by other high priority suppliers
                        if (location.getCountry() == null && acmeHotelResult.getCountry() != null) {
                            location.setCountry(StringUtils.capitalize(acmeHotelResult.getCountry()));
                        }
                    }
                    case PatagoniaHotelResult patagoniaHotelResult -> {
                        if (patagoniaHotelResult.getLatitude() != 0) {
                            location.setLat(patagoniaHotelResult.getLatitude());
                        }
                        if (patagoniaHotelResult.getLongitude() != 0) {
                            location.setLng(patagoniaHotelResult.getLongitude());
                        }
                        if (patagoniaHotelResult.getAddress() != null && !patagoniaHotelResult.getAddress().isBlank()) {
                            location.setAddress(StringUtils.capitalize(patagoniaHotelResult.getAddress()));
                        }
                    }
                    case PaperfliesHotelResult paperfliesHotelResult -> {
                        if (paperfliesHotelResult.getLocation() != null) {
                            // only set the address if this is not already set by other high priority suppliers
                            if (location.getAddress() == null && paperfliesHotelResult.getLocation().getAddress() != null && !paperfliesHotelResult.getLocation().getAddress().isBlank()) {
                                location.setAddress(StringUtils.capitalize(paperfliesHotelResult.getLocation().getAddress()));
                            }
                            if (paperfliesHotelResult.getLocation().getCountry() != null && !paperfliesHotelResult.getLocation().getCountry().isBlank()) {
                                location.setCountry(StringUtils.capitalize(paperfliesHotelResult.getLocation().getCountry()));
                            }
                        }
                    }
                    default -> LOGGER.log(Level.WARNING, "unidentified class instance");
                }
            }
        }

        return location;
    }

    @Override
    public String resolveDescription() {
        // description with the highest details will be returned
        String description = "";

        for (SupplierHotel hotel : this.supplierHotels) {
            switch (hotel) {
                case AcmeHotelResult acmeHotelResult -> {
                    if (acmeHotelResult.getDescription() != null && acmeHotelResult.getDescription().length() > description.length()) {
                        description = acmeHotelResult.getDescription();
                    }
                }
                case PatagoniaHotelResult patagoniaHotelResult -> {
                    if (patagoniaHotelResult.getInfo() != null && patagoniaHotelResult.getInfo().length() > description.length()) {
                        description = patagoniaHotelResult.getInfo();
                    }
                }
                case PaperfliesHotelResult paperfliesHotelResult -> {
                    if (paperfliesHotelResult.getDetails() != null && paperfliesHotelResult.getDetails().length() > description.length()) {
                        description = paperfliesHotelResult.getDetails();
                    }
                }
                default -> LOGGER.log(Level.WARNING, "unidentified class instance");
            }
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

        // define the priority order to consider when resolving for amenities
        List<Class<?>> priorityOrder = List.of(PaperfliesHotelResult.class, PatagoniaHotelResult.class, AcmeHotelResult.class);

        for (Class<?> prioritizedType : priorityOrder) {
            for (SupplierHotel hotel : this.supplierHotels) {
                if (!prioritizedType.isInstance(hotel)) {
                    continue;
                }
                switch (hotel) {
                    case AcmeHotelResult acmeHotelResult -> {
                        // collect amenities from acme hotels and add to general amenities as they align best with general amenities.
                        // If the amenity is already present as a room amenity, do not add to general amenity again.
                        if (acmeHotelResult.getFacilities() != null) {
                            for (String ga : acmeHotelResult.getFacilities()) {
                                String key = getKey(ga);
                                if (!generalAmenities.contains(key) && !roomAmenities.contains(key)) {
                                    generalAmenities.add(key);
                                    amenities.getGeneral().add(StringUtils.capitalize(ga.strip()));
                                }
                            }
                        }
                    }
                    case PatagoniaHotelResult patagoniaHotelResult -> {
                        // collect amenities from patagonia hotels and add to room amenities as they align best to room amenities.
                        // If the amenity is already present as a general amenity, do not add to room amenity again.
                        if (patagoniaHotelResult.getAmenities() != null) {
                            for (String ga : patagoniaHotelResult.getAmenities()) {
                                String key = getKey(ga);
                                if (!generalAmenities.contains(key) && !roomAmenities.contains(key)) {
                                    roomAmenities.add(key);
                                    amenities.getRoom().add(StringUtils.capitalize(ga.strip()));
                                }
                            }
                        }
                    }
                    case PaperfliesHotelResult paperfliesHotelResult -> {
                        // paperflies hotels have separate general and room amenities. give first priority to it
                        if (paperfliesHotelResult.getAmenities() != null) {
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
                    }
                    default -> LOGGER.log(Level.WARNING, "unidentified class instance");
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

        // define the priority order to consider when resolving for images
        List<Class<?>> priorityOrder = List.of(PaperfliesHotelResult.class, PatagoniaHotelResult.class);

        for (Class<?> prioritizedType : priorityOrder) {
            for (SupplierHotel hotel : this.supplierHotels) {
                if (!prioritizedType.isInstance(hotel)) {
                    continue;
                }
                switch (hotel) {
                    case PatagoniaHotelResult patagoniaHotelResult -> {
                        if (patagoniaHotelResult.getImages() != null) {
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
                    }
                    case PaperfliesHotelResult paperfliesHotelResult -> {
                        if (paperfliesHotelResult.getImages() != null) {
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
                    }
                    default -> LOGGER.log(Level.WARNING, "unidentified class instance");
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
        List<String> bookingConditions = new ArrayList<>();

        // only paperflies hotels have booking conditions
        List<Class<?>> priorityOrder = List.of(PaperfliesHotelResult.class);

        for (Class<?> prioritizedType : priorityOrder) {
            for (SupplierHotel hotel : this.supplierHotels) {
                if (!prioritizedType.isInstance(hotel)) {
                    continue;
                }
                if (hotel instanceof PaperfliesHotelResult paperfliesHotelResult) {
                    if (paperfliesHotelResult.getBookingConditions() != null && !paperfliesHotelResult.getBookingConditions().isEmpty()) {
                        for (String condition : paperfliesHotelResult.getBookingConditions()) {
                            if (condition != null && !condition.isBlank()) {
                                String[] splits = condition.split("===");
                                for (String split : splits) {
                                    bookingConditions.add(StringUtils.capitalize(split.strip()));
                                }
                            }
                        }
                    }
                } else {
                    LOGGER.log(Level.WARNING, "unidentified class instance");
                }
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
        return sb.toString().toLowerCase();
    }
}
