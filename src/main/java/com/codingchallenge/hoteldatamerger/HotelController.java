package com.codingchallenge.hoteldatamerger;

import com.codingchallenge.hoteldatamerger.model.HotelResult;
import com.codingchallenge.hoteldatamerger.service.HotelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public List<HotelResult> getHotels() {
        return this.hotelService.getAllHotels();
    }
}
