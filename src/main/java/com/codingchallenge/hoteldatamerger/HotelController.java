package com.codingchallenge.hoteldatamerger;

import com.codingchallenge.hoteldatamerger.model.HotelResult;
import com.codingchallenge.hoteldatamerger.sanitizer.InputSanitizer;
import com.codingchallenge.hoteldatamerger.service.HotelService;
import com.codingchallenge.hoteldatamerger.service.PaginatedHotelResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public PaginatedHotelResponse getHotels(
            @RequestParam(value = "destinationIDs", required = false) List<String> destinations,
            @RequestParam(value = "hotelIDs", required = false) List<String> hotelIDs,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<String> sanitizedDestinationIDs = InputSanitizer.sanitizeStringList(destinations);
        List<String> sanitizedHotelIDs = InputSanitizer.sanitizeStringList(hotelIDs);

        // Get paginated results supporting hateaos
        return hotelService.getHotels(sanitizedDestinationIDs, sanitizedHotelIDs, limit, offset);
    }

    @GetMapping("/{hotelID}")
    public EntityModel<HotelResult> getHotelById(@PathVariable String hotelID) {
        if (hotelID == null || hotelID.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mandatory hotel id not provided");
        }
        // Fetch hotel by its ID
        HotelResult hotel = hotelService.getHotelById(hotelID);

        if (hotel == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found for id: " + hotelID);
        }

        // add self links supporting hateaos
        EntityModel<HotelResult> hotelModel = EntityModel.of(hotel);
        hotelModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HotelController.class)
                .getHotelById(hotelID)).withSelfRel());

        return hotelModel;
    }
}
