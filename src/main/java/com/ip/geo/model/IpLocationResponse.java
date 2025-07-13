package com.ip.geo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;


@Data
public class IpLocationResponse {

    @JsonProperty("ipAddress")
    private String ipAddress;

    @JsonProperty("continent")
    private String continent;

    @JsonProperty("countryName")
    private String country;

    @JsonProperty("regionName")
    private String region;

    @JsonProperty("cityName")
    private String city;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("longitude")
    private double longitude;

    public boolean isValid() {
        return ipAddress != null
                && country != null && !country.isBlank()
                && continent != null && !continent.isBlank()
                && !(latitude == 0.0 && longitude == 0.0);
    }

}
