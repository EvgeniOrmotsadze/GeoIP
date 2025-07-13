package com.ip.geo.cache;

import com.ip.geo.model.IpLocationResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IpLocationCacheTest {

    private final IpLocationCache cache = new IpLocationCache();

    @Test
    void returnsNullIfNotPresent() {
        assertNull(cache.get("136.159.0.0"));
    }

    @Test
    void returnsCachedEntry() {
        String ip = "136.159.0.0";
        IpLocationResponse response = new IpLocationResponse();
        cache.put(ip, response);
        assertEquals(response, cache.get(ip));
    }
}
