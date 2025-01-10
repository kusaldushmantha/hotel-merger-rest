package com.codingchallenge.hoteldatamerger;

import com.codingchallenge.hoteldatamerger.model.HotelResult;
import com.codingchallenge.hoteldatamerger.sanitizer.Sanitize;
import com.codingchallenge.hoteldatamerger.service.HotelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public List<HotelResult> getHotels(
            @RequestParam(value = "destinationIDs", required = false) List<String> destinations,
            @RequestParam(value = "hotelIDs", required = false) List<String> hotelIDs) {

        List<String> sanitizedDestinationIDs = Sanitize.sanitizeStringList(destinations);
        List<String> sanitizedHotelIDs = Sanitize.sanitizeStringList(hotelIDs);

        return this.hotelService.getHotels(new HashSet<>(sanitizedDestinationIDs), new HashSet<>(sanitizedHotelIDs));
    }
}