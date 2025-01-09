package com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.acme;

import com.codingchallenge.hoteldatamerger.hotelresultcollector.suppliers.SupplierHotel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
* AcmeHotelResult is the entity representing the JSON response from Acme supplier
*/
@Getter
@Setter
public class AcmeHotelResult implements SupplierHotel {
    @JsonProperty("Id")
    private String ID;

    @JsonProperty("DestinationId")
    private String destinationID;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Latitude")
    private float latitude;

    @JsonProperty("Longitude")
    private float longitude;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("PostalCode")
    private String postalCode;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Facilities")
    private List<String> facilities;
}
