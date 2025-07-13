package com.ip.geo.service;


import com.ip.geo.cache.IpLocationCache;
import com.ip.geo.model.IpLocationResponse;
import com.ip.geo.service.RateLimiterQueueManager;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IpLookupServiceTest {

    private final IpLocationCache cache = mock(IpLocationCache.class);
    private final RateLimiterQueueManager queueManager = mock(RateLimiterQueueManager.class);
    private final IpLookupService service = new IpLookupService(cache, queueManager);

    @Test
    void returnsCachedResponse() {
        String ip = "136.159.0.0";
        IpLocationResponse cached = new IpLocationResponse();
        when(cache.get(ip)).thenReturn(cached);

        Optional<IpLocationResponse> result = service.getIpLocation(ip);

        assertTrue(result.isPresent());
        assertEquals(cached, result.get());
        verifyNoInteractions(queueManager);
    }

    @Test
    void returnsQueueResponse() {
        String ip = "136.159.0.0";
        when(cache.get(ip)).thenReturn(null);
        IpLocationResponse fetched = new IpLocationResponse();
        when(queueManager.submit(ip)).thenReturn(CompletableFuture.completedFuture(fetched));

        Optional<IpLocationResponse> result = service.getIpLocation(ip);

        assertTrue(result.isPresent());
        assertEquals(fetched, result.get());
    }

    @Test
    void returnsEmptyOnQueueTimeout() {
        String ip = "136.159.0.0";
        when(cache.get(ip)).thenReturn(null);
        CompletableFuture<IpLocationResponse> future = new CompletableFuture<>();
        when(queueManager.submit(ip)).thenReturn(future);

        Optional<IpLocationResponse> result = service.getIpLocation(ip);

        assertTrue(result.isEmpty());
    }
}

