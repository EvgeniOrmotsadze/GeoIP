# 📍 IP Geolocation Service

A Java Spring Boot microservice that accepts an IP address and returns its geolocation using an external public API. This service includes caching, rate-limiting, validation, and concurrent request handling.

---

##  Features

-  Lookup geolocation data from IP address
-  Caches responses for 30 days to avoid redundant API calls
-  Supports multiple concurrent requests
-  Rate-limited (1 request per second) to comply with FreeIPAPI usage policy
-  Unit tests for controller, service, client, cache, and queue manager
-  External API URL is configurable

---

## Technologies Used

- Java 17
- Spring Boot 3.x
- Spring WebFlux (WebClient)
- SLF4J (Logging)
- JUnit 5 + Mockito
- Concurrent Java (`BlockingQueue`, `CompletableFuture`, `ScheduledExecutorService`)

---

## API ENDPOINT
- GET /api/geo?ip={ipAddress} 
- success response
`  {
  "ipAddress": "136.159.0.0",
  "continent": "Americas",
  "country": "Canada",
  "region": "Alberta",
  "city": "Calgary (Northwest Calgary)",
  "latitude": 51.0802,
  "longitude": -114.13
  }`
- error response
  `400 Bad Request — Invalid IP format `
  `404 Not found — if Ips not found in provider DB`
  `502 Gateway Timeout — External API timed out or failed`

## Running the Application
- ./gradlew bootRun
## Running Tests
- ./gradlew test
