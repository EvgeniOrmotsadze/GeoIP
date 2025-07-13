package com.ip.geo.cache;

import com.ip.geo.model.IpLocationResponse;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IpLocationCache {

    private static final Duration EXPIRATION_DURATION = Duration.ofDays(30);

    private final Map<String, CachedEntry> cache = new ConcurrentHashMap<>();

    public IpLocationResponse get(String ip) {
        CachedEntry entry = cache.get(ip);
        if (entry != null && !entry.isExpired()) {
            return entry.response();
        }
        cache.remove(ip);
        return null;
    }

    public void put(String ip, IpLocationResponse response) {
        cache.put(ip, new CachedEntry(response, Instant.now()));
    }

    private record CachedEntry(IpLocationResponse response, Instant timestamp) {
        boolean isExpired() {
            return Instant.now().isAfter(timestamp.plus(EXPIRATION_DURATION));
        }
    }
}
