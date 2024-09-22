package com.homeflix.app.data.service;

import lombok.Data;

@Data
public class GeoDataRequest {
    private String ip;
    private String isp;
    private String org;
    private String hostname;
    private Double latitude;
    private Double longitude;
    private String postal_code;
    private String city;
    private String country_code;
    private String country_name;
    private String continent_code;
    private String continent_name;
    private String region;
    private String district;
    private String timezone_name;
    private String connection_type;
    private Double asn_number;
    private String asn_org;
    private String currency_code;
    private String currency_name;
    private boolean success;
    private boolean premium;
}
