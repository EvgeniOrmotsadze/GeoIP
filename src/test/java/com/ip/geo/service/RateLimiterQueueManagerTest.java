package com.ip.geo.service;

import com.ip.geo.cache.IpLocationCache;
import com.ip.geo.provider.FreeIpApiClient;
import com.ip.geo.model.IpLocationResponse;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RateLimiterQueueManagerTest {

    private final FreeIpApiClient client = mock(FreeIpApiClient.class);
    private final IpLocationCache cache = mock(IpLocationCache.class);
    private final RateLimiterQueueManager queueManager = new RateLimiterQueueManager(client, cache);

    @Test
    void submitNewIpReturnsFuture() {
        CompletableFuture<IpLocationResponse> future = queueManager.submit("136.159.0.0");
        assertNotNull(future);
    }

    @Test
    void submitSameIpReturnsExistingFuture() {
        CompletableFuture<IpLocationResponse> future1 = queueManager.submit("136.159.0.0");
        CompletableFuture<IpLocationResponse> future2 = queueManager.submit("136.159.0.0");

        assertSame(future1, future2);
    }
}