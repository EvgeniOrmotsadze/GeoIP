package com.ip.geo.controller;

import com.ip.geo.model.IpLocationResponse;
import com.ip.geo.service.IpLookupService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IpLookupControllerTest {

    private final IpLookupService service = mock(IpLookupService.class);
    private final IpLookupController controller = new IpLookupController(service);

    @Test
    void validIpReturns200() {
        String ip = "136.159.0.0";

        IpLocationResponse expected = new IpLocationResponse();
        expected.setIpAddress(ip);
        expected.setContinent("Americas");
        expected.setCountry("Canada");
        expected.setRegion("Alberta");
        expected.setCity("Calgary");
        expected.setLatitude(51.075153);
        expected.setLongitude(-114.12841);

        when(service.getIpLocation(ip)).thenReturn(Optional.of(expected));

        ResponseEntity<?> response = controller.lookup(ip);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    void invalidIpReturns400() {
        String ip = "invalid-ip";

        ResponseEntity<?> response = controller.lookup(ip);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid IP address format", response.getBody());
    }

    @Test
    void ipServiceFailureReturns404() {
        String ip = "136.159.0.0";

        // Mock service to return empty (Optional.empty())
        when(service.getIpLocation(ip)).thenReturn(Optional.empty());

        // Execute controller method
        ResponseEntity<?> response = controller.lookup(ip);

        // Validate response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }
}