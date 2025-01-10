package com.codingchallenge.hoteldatamerger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelImage {
    @JsonProperty("link")
    private String link;

    @JsonProperty("description")
    private String description;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HotelImage that)) return false;
        return getLink().strip().equals(that.getLink())
                && getDescription().strip().replaceAll("\\s+", "").equalsIgnoreCase(that.getDescription().strip().replaceAll("\\s+", ""));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLink(), getDescription());
    }
}
