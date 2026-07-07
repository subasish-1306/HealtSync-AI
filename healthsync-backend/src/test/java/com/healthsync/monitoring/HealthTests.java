package com.healthsync.monitoring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

/**
 * Integration test validating the heartbeat endpoint.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class HealthTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHealthEndpointReturnsStatusUp() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/health", Map.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("UP", response.getBody().get("applicationStatus"));
        Assertions.assertEquals("UP", response.getBody().get("databaseStatus"));
        Assertions.assertNotNull(response.getBody().get("version"));
        Assertions.assertNotNull(response.getBody().get("timestamp"));
    }
}
