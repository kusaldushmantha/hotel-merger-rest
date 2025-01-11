package com.codingchallenge.hoteldatamerger.merger;

import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.SupplierHotel;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.acme.AcmeHotelResult;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.paperflies.*;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.patagonia.PatagoniaHotelImage;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.patagonia.PatagoniaHotelImages;
import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.patagonia.PatagoniaHotelResult;
import com.codingchallenge.hoteldatamerger.model.HotelAmenities;
import com.codingchallenge.hoteldatamerger.model.HotelImages;
import com.codingchallenge.hoteldatamerger.model.HotelLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleHotelAttributeResolverTest {

    private List<SupplierHotel> supplierHotels;
    private SimpleHotelAttributeResolver resolver;

    @BeforeEach
    void setUp() {
        supplierHotels = new ArrayList<>();
        resolver = new SimpleHotelAttributeResolver(supplierHotels);
    }

    @Test
    void testResolveId() {
        AcmeHotelResult acmeHotel = new AcmeHotelResult();
        acmeHotel.setID("123");
        PaperfliesHotelResult paperfliesHotel = new PaperfliesHotelResult();
        supplierHotels.add(acmeHotel);
        supplierHotels.add(paperfliesHotel);

        assertEquals("123", resolver.resolveId());
    }

    @Test
    void testResolveDestinationId() {
        AcmeHotelResult acmeHotel = new AcmeHotelResult();
        acmeHotel.setDestinationID("789");
        PatagoniaHotelResult patagoniaHotel = new PatagoniaHotelResult();
        supplierHotels.add(patagoniaHotel);
        supplierHotels.add(acmeHotel);

        assertEquals(789, resolver.resolveDestinationId());
    }

    @Test
    void testResolveName() {
        PaperfliesHotelResult paperfliesHotel = new PaperfliesHotelResult();
        paperfliesHotel.setName("Hotel Paperflies");

        supplierHotels.add(paperfliesHotel);

        assertEquals("Hotel Paperflies", resolver.resolveName());
    }

    @Test
    void testResolveLocationWithAcmeHotelResult() {
        AcmeHotelResult acmeHotelResult = Mockito.mock(AcmeHotelResult.class);
        Mockito.when(acmeHotelResult.getLatitude()).thenReturn(10.123f);
        Mockito.when(acmeHotelResult.getLongitude()).thenReturn(20.456f);
        Mockito.when(acmeHotelResult.getAddress()).thenReturn("123 Main Street");
        Mockito.when(acmeHotelResult.getCity()).thenReturn("Test City");
        Mockito.when(acmeHotelResult.getCountry()).thenReturn("Test Country");

        SimpleHotelAttributeResolver resolver = new SimpleHotelAttributeResolver(List.of(acmeHotelResult));
        HotelLocation location = resolver.resolveLocation();

        assertEquals(10.123f, location.getLat());
        assertEquals(20.456f, location.getLng());
        assertEquals("123 Main Street", location.getAddress());
        assertEquals("Test City", location.getCity());
        assertEquals("Test Country", location.getCountry());
    }

    @Test
    void testResolveLocationWithPatagoniaHotelResult() {
        PatagoniaHotelResult patagoniaHotelResult = Mockito.mock(PatagoniaHotelResult.class);
        Mockito.when(patagoniaHotelResult.getLatitude()).thenReturn(15.789f);
        Mockito.when(patagoniaHotelResult.getLongitude()).thenReturn(25.987f);
        Mockito.when(patagoniaHotelResult.getAddress()).thenReturn("456 Elm Street");

        SimpleHotelAttributeResolver resolver = new SimpleHotelAttributeResolver(List.of(patagoniaHotelResult));
        HotelLocation location = resolver.resolveLocation();

        assertEquals(15.789f, location.getLat());
        assertEquals(25.987f, location.getLng());
        assertEquals("456 Elm Street", location.getAddress());
        assertNull(location.getCity());
        assertNull(location.getCountry());
    }

    @Test
    void testResolveLocationWithPaperfliesHotelResult() {
        PaperfliesHotelResult paperfliesHotelResult = Mockito.mock(PaperfliesHotelResult.class);
        PaperfliesHotelLocation mockLocation = Mockito.mock(PaperfliesHotelLocation.class);
        Mockito.when(mockLocation.getAddress()).thenReturn("789 Oak Street");
        Mockito.when(mockLocation.getCountry()).thenReturn("Mock Country");
        Mockito.when(paperfliesHotelResult.getLocation()).thenReturn(mockLocation);

        SimpleHotelAttributeResolver resolver = new SimpleHotelAttributeResolver(List.of(paperfliesHotelResult));
        HotelLocation location = resolver.resolveLocation();

        assertEquals("789 Oak Street", location.getAddress());
        assertEquals("Mock Country", location.getCountry());
        assertEquals(0, location.getLat());
        assertEquals(0, location.getLng());
        assertNull(location.getCity());
    }

    @Test
    void testResolveLocationWithMixedResults() {
        AcmeHotelResult acmeHotelResult = Mockito.mock(AcmeHotelResult.class);
        Mockito.when(acmeHotelResult.getLatitude()).thenReturn(12.345f);
        Mockito.when(acmeHotelResult.getLongitude()).thenReturn(67.890f);
        Mockito.when(acmeHotelResult.getAddress()).thenReturn("Acme Address");
        Mockito.when(acmeHotelResult.getCity()).thenReturn("Acme City");
        Mockito.when(acmeHotelResult.getCountry()).thenReturn("Acme Country");

        PatagoniaHotelResult patagoniaHotelResult = Mockito.mock(PatagoniaHotelResult.class);
        Mockito.when(patagoniaHotelResult.getLatitude()).thenReturn(98.765f);
        Mockito.when(patagoniaHotelResult.getLongitude()).thenReturn(43.210f);
        Mockito.when(patagoniaHotelResult.getAddress()).thenReturn("Patagonia Address");

        PaperfliesHotelResult paperfliesHotelResult = Mockito.mock(PaperfliesHotelResult.class);
        PaperfliesHotelLocation mockLocation = Mockito.mock(PaperfliesHotelLocation.class);
        Mockito.when(mockLocation.getAddress()).thenReturn("Paperflies Address");
        Mockito.when(mockLocation.getCountry()).thenReturn("Paperflies Country");
        Mockito.when(paperfliesHotelResult.getLocation()).thenReturn(mockLocation);

        SimpleHotelAttributeResolver resolver = new SimpleHotelAttributeResolver(List.of(acmeHotelResult, patagoniaHotelResult, paperfliesHotelResult));
        HotelLocation location = resolver.resolveLocation();

        assertEquals(98.765f, location.getLat()); // Patagonia result takes priority for latitude
        assertEquals(43.210f, location.getLng()); // Patagonia result takes priority for longitude
        assertEquals("Patagonia Address", location.getAddress()); // Patagonia result address takes priority
        assertEquals("Acme City", location.getCity()); // Acme city gets priority
        assertEquals("Paperflies Country", location.getCountry()); // Paperflies country gets priority
    }

    @Test
    void testResolveLocationWithNoData() {
        SimpleHotelAttributeResolver resolver = new SimpleHotelAttributeResolver(List.of());
        HotelLocation location = resolver.resolveLocation();

        assertEquals(0, location.getLat());
        assertEquals(0, location.getLng());
        assertNull(location.getAddress());
        assertNull(location.getCity());
        assertNull(location.getCountry());
    }

    @Test
    void testResolveDescription() {
        AcmeHotelResult acmeHotel = new AcmeHotelResult();
        acmeHotel.setDescription("Short description");

        PaperfliesHotelResult paperfliesHotel = new PaperfliesHotelResult();
        paperfliesHotel.setDetails("A much longer and detailed description");

        supplierHotels.add(acmeHotel);
        supplierHotels.add(paperfliesHotel);

        assertEquals("A much longer and detailed description", resolver.resolveDescription());
    }

    @Test
    void testResolveAmenities() {
        AcmeHotelResult acmeHotel = new AcmeHotelResult();
        acmeHotel.setFacilities(List.of("Free WiFi", "Parking"));

        PaperfliesHotelResult paperfliesHotel = new PaperfliesHotelResult();
        PaperfliesHotelAmenities paperfliesAmenities = new PaperfliesHotelAmenities();
        paperfliesAmenities.setGeneral(List.of("Swimming Pool"));
        paperfliesAmenities.setRoom(List.of("Air Conditioning"));
        paperfliesHotel.setAmenities(paperfliesAmenities);

        supplierHotels.add(acmeHotel);
        supplierHotels.add(paperfliesHotel);

        HotelAmenities amenities = resolver.resolveAmenities();

        assertEquals(3, amenities.getGeneral().size());
        assertEquals(1, amenities.getRoom().size());
        assertTrue(amenities.getGeneral().containsAll(List.of("Swimming Pool", "Free WiFi", "Parking")));
        assertTrue(amenities.getRoom().contains("Air Conditioning"));
    }

    @Test
    void testResolveImages_withEmptySupplierHotels() {
        PatagoniaHotelResult patagoniaHotelResult = Mockito.mock(PatagoniaHotelResult.class);
        Mockito.when(patagoniaHotelResult.getImages()).thenReturn(null);

        PaperfliesHotelResult paperfliesHotelResult = Mockito.mock(PaperfliesHotelResult.class);
        Mockito.when(paperfliesHotelResult.getImages()).thenReturn(null);

        SimpleHotelAttributeResolver resolver = new SimpleHotelAttributeResolver(List.of(patagoniaHotelResult, paperfliesHotelResult));
        HotelImages images = resolver.resolveImages();

        assertTrue(images.getSiteImages().isEmpty());
        assertTrue(images.getAmenitiesImages().isEmpty());
        assertTrue(images.getRoomImages().isEmpty());
    }

    @Test
    void testResolveImages_withMixedSupplierHotels() {
        PaperfliesHotelResult paperfliesHotelResult = Mockito.mock(PaperfliesHotelResult.class);
        PaperfliesHotelImages paperfliesHotelImages = Mockito.mock(PaperfliesHotelImages.class);
        PaperfliesHotelImage siteImage1 = new PaperfliesHotelImage();
        siteImage1.setCaption("siteCaption");
        siteImage1.setLink("siteLink");

        PatagoniaHotelResult patagoniaHotelResult = Mockito.mock(PatagoniaHotelResult.class);
        PatagoniaHotelImages patagoniaHotelImages = Mockito.mock(PatagoniaHotelImages.class);
        PatagoniaHotelImage roomImage = new PatagoniaHotelImage();
        roomImage.setDescription("roomDescription");
        roomImage.setUrl("roomURL");

        Mockito.when(paperfliesHotelResult.getImages()).thenReturn(paperfliesHotelImages);
        Mockito.when(paperfliesHotelImages.getSite()).thenReturn(List.of(siteImage1));

        Mockito.when(patagoniaHotelResult.getImages()).thenReturn(patagoniaHotelImages);
        Mockito.when(patagoniaHotelImages.getRooms()).thenReturn(List.of(roomImage));

        SimpleHotelAttributeResolver resolver = new SimpleHotelAttributeResolver(List.of(patagoniaHotelResult, paperfliesHotelResult));
        HotelImages images = resolver.resolveImages();

        assertEquals(1, images.getSiteImages().size());
        assertEquals("siteLink", images.getSiteImages().getFirst().getLink());
        assertEquals("SiteCaption", images.getSiteImages().getFirst().getDescription());

        assertEquals(1, images.getRoomImages().size());
        assertEquals("roomURL", images.getRoomImages().getFirst().getLink());
        assertEquals("RoomDescription", images.getRoomImages().getFirst().getDescription());
    }

    @Test
    void testResolveBookingConditions() {
        PaperfliesHotelResult paperfliesHotel = new PaperfliesHotelResult();
        paperfliesHotel.setBookingConditions(List.of(
                "All children are welcome. One child under 12 years stays free of charge when using existing beds. One child under 2 years stays free of charge in a child's cot/crib. One child under 4 years stays free of charge when using existing beds. One older child or adult is charged SGD 82.39 per person per night in an extra bed. The maximum number of children's cots/cribs in a room is 1. There is no capacity for extra beds in the room.",
                "Pets are not allowed.",
                "WiFi is available in all areas and is free of charge.",
                "Free private parking is possible on site (reservation is not needed).",
                "Guests are required to show a photo identification and credit card upon check-in. Please note that all Special Requests are subject to availability and additional charges may apply. Payment before arrival via bank transfer is required. The property will contact you after you book to provide instructions. Please note that the full amount of the reservation is due before arrival. Resorts World Sentosa will send a confirmation with detailed payment information. After full payment is taken, the property's details, including the address and where to collect keys, will be emailed to you. Bag checks will be conducted prior to entry to Adventure Cove Waterpark. === Upon check-in, guests will be provided with complimentary Sentosa Pass (monorail) to enjoy unlimited transportation between Sentosa Island and Harbour Front (VivoCity). === Prepayment for non refundable bookings will be charged by RWS Call Centre. === All guests can enjoy complimentary parking during their stay, limited to one exit from the hotel per day. === Room reservation charges will be charged upon check-in. Credit card provided upon reservation is for guarantee purpose. === For reservations made with inclusive breakfast, please note that breakfast is applicable only for number of adults paid in the room rate. Any children or additional adults are charged separately for breakfast and are to paid directly to the hotel."
        ));

        List<String> expected = List.of(
                "All children are welcome. One child under 12 years stays free of charge when using existing beds. One child under 2 years stays free of charge in a child's cot/crib. One child under 4 years stays free of charge when using existing beds. One older child or adult is charged SGD 82.39 per person per night in an extra bed. The maximum number of children's cots/cribs in a room is 1. There is no capacity for extra beds in the room.",
                "Pets are not allowed.",
                "WiFi is available in all areas and is free of charge.",
                "Free private parking is possible on site (reservation is not needed).",
                "Guests are required to show a photo identification and credit card upon check-in. Please note that all Special Requests are subject to availability and additional charges may apply. Payment before arrival via bank transfer is required. The property will contact you after you book to provide instructions. Please note that the full amount of the reservation is due before arrival. Resorts World Sentosa will send a confirmation with detailed payment information. After full payment is taken, the property's details, including the address and where to collect keys, will be emailed to you. Bag checks will be conducted prior to entry to Adventure Cove Waterpark.",
                "Upon check-in, guests will be provided with complimentary Sentosa Pass (monorail) to enjoy unlimited transportation between Sentosa Island and Harbour Front (VivoCity).",
                "Prepayment for non refundable bookings will be charged by RWS Call Centre.",
                "All guests can enjoy complimentary parking during their stay, limited to one exit from the hotel per day.",
                "Room reservation charges will be charged upon check-in. Credit card provided upon reservation is for guarantee purpose.",
                "For reservations made with inclusive breakfast, please note that breakfast is applicable only for number of adults paid in the room rate. Any children or additional adults are charged separately for breakfast and are to paid directly to the hotel.");

        supplierHotels.add(paperfliesHotel);

        List<String> conditions = resolver.resolveBookingConditions();

        assertEquals(10, conditions.size());
        assertLinesMatch(expected, conditions);
    }
}
