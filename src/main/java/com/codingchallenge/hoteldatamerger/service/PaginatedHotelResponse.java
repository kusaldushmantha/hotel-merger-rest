package com.codingchallenge.hoteldatamerger.service;

import com.codingchallenge.hoteldatamerger.HotelController;
import com.codingchallenge.hoteldatamerger.model.HotelResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.List;

@Getter
@Setter
public class PaginatedHotelResponse {
    private List<EntityModel<HotelResult>> hotels;
    private int totalCount;
    private int limit;
    private int offset;
    private Link next;
    private Link prev;

    public PaginatedHotelResponse(List<HotelResult> hotels, int totalCount, int limit, int offset) {
        this.hotels = hotels.stream()
                .map(hotelResult -> {
                    EntityModel<HotelResult> hotelModel = EntityModel.of(hotelResult);
                    hotelModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HotelController.class)
                            .getHotelById(hotelResult.getId())).withSelfRel());
                    return hotelModel;
                })
                .toList();

        this.totalCount = totalCount;
        this.limit = limit;
        this.offset = offset;
    }

    public void addNextLink(int limit, int offset, List<String> destinationIDs, List<String> hotelIDs) {
        int nextOffset = offset + limit;
        if (nextOffset < totalCount) {
            this.next = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HotelController.class)
                            .getHotels(destinationIDs, hotelIDs, limit, nextOffset))
                    .withRel("next");
        }
    }

    // Add previous link
    public void addPrevLink(int limit, int offset, List<String> destinationIDs, List<String> hotelIDs) {
        int prevOffset = offset - limit;
        if (prevOffset >= 0) {
            this.prev = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HotelController.class)
                            .getHotels(destinationIDs, hotelIDs, limit, prevOffset))
                    .withRel("prev");
        }
    }
}
