package com.codingchallenge.hoteldatamerger.hotelresultcollector;

import java.util.List;

public interface HotelResultCollector<T> {
    List<T> getAllHotels();
}
