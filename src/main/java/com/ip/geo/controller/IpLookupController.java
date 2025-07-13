package com.ip.geo.controller;

import com.ip.geo.model.IpLocationResponse;
import com.ip.geo.service.IpLookupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/geo")
public class IpLookupController {

    private final IpLookupService ipLookupService;

    public IpLookupController(IpLookupService ipLookupService) {
        this.ipLookupService = ipLookupService;
    }

    @GetMapping
    public ResponseEntity<?> lookup(@RequestParam String ip) {
        if (!isValidIp(ip)) {
            return ResponseEntity.badRequest().body("Invalid IP address format");
        }

        return ipLookupService.getIpLocation(ip)
                .filter(IpLocationResponse::isValid)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of("error", "IP address not found in geo database"))
                );

    }

    private boolean isValidIp(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return !address.isAnyLocalAddress() && !address.isLoopbackAddress();
        } catch (UnknownHostException ex) {
            return false;
        }
    }
}
