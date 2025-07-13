package com.ip.geo.provider;

import com.ip.geo.model.IpLocationResponse;

public interface GeoIpProvider {
    IpLocationResponse fetch(String ip);
}