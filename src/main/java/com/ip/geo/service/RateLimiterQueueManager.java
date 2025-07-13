package com.ip.geo.service;

import com.ip.geo.cache.IpLocationCache;
import com.ip.geo.provider.FreeIpApiClient;
import com.ip.geo.model.IpLocationResponse;
import com.ip.geo.provider.GeoIpProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Component
public class RateLimiterQueueManager {

    private final BlockingQueue<String> requestQueue = new LinkedBlockingQueue<>();
    private final Map<String, CompletableFuture<IpLocationResponse>> pendingRequests = new ConcurrentHashMap<>();

    private final GeoIpProvider provider;
    private final IpLocationCache cache;

    public RateLimiterQueueManager(GeoIpProvider provider, IpLocationCache cache) {
        this.provider = provider;
        this.cache = cache;
    }

    /**
     * Submit an IP for retrieval.
     * @param ip IP address to look up
     * @return CompletableFuture that will contain the IP response
     */
    public CompletableFuture<IpLocationResponse> submit(String ip) {
        return pendingRequests.computeIfAbsent(ip, k -> {
            requestQueue.offer(ip);
            log.info("IP {} added to queue", ip);
            return new CompletableFuture<>();
        });
    }

    /**
     * Scheduled worker that fetches IP data from the external API at 1 request per second.
     */
    @PostConstruct
    private void startWorker() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                String ip = requestQueue.poll();
                if (ip != null) {
                    log.info("Processing queued IP request: {}", ip);
                    IpLocationResponse response = provider.fetch(ip);
                    if (response != null) {
                        cache.put(ip, response);
                        pendingRequests.remove(ip).complete(response);
                        log.info("Successfully fetched and cached IP data for {}", ip);
                    } else {
                        pendingRequests.remove(ip).completeExceptionally(new RuntimeException("Failed to retrieve IP data"));
                        log.error("Failed to fetch IP data for {}", ip);
                    }
                }
            } catch (Exception e) {
                log.error("Unexpected error in rate limiter worker: {}", e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
