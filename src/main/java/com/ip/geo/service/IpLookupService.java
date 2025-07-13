package com.ip.geo.service;

import com.ip.geo.cache.IpLocationCache;
import com.ip.geo.model.IpLocationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class IpLookupService {

    private final IpLocationCache cache;
    private final RateLimiterQueueManager queueManager;

    public IpLookupService(IpLocationCache cache, RateLimiterQueueManager queueManager) {
        this.cache = cache;
        this.queueManager = queueManager;
    }

    public Optional<IpLocationResponse> getIpLocation(String ip) {
        IpLocationResponse cached = cache.get(ip);
        if (cached != null) return Optional.of(cached);

        try {
            CompletableFuture<IpLocationResponse> future = queueManager.submit(ip);
            return Optional.ofNullable(future.get(10, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            log.error("Timeout retrieving data for IP: {}", ip);
        } catch (Exception e) {
            log.error("Error retrieving IP data for {}: {}", ip, e.getMessage());
        }
        return Optional.empty();
    }
}

