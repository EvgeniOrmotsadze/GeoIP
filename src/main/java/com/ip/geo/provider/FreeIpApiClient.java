package com.ip.geo.provider;

import com.ip.geo.model.IpLocationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@Component
public class FreeIpApiClient implements GeoIpProvider{

    private final WebClient webClient;

    public FreeIpApiClient(@Value("${external.api.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public IpLocationResponse fetch(String ip) {
        try {
            return webClient.get()
                    .uri("/api/json/{ip}", ip)
                    .retrieve()
                    .bodyToMono(IpLocationResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Error fetching IP data for {}: {}", ip, e.getMessage());
            throw e;
        }
    }
}