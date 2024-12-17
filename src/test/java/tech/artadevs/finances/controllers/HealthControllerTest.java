package tech.artadevs.finances.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @SuppressWarnings("null")
    @Test
    void testHealthEndpoint() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.getForEntity("/health", Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("healthy", response.getBody().get("status"));
    }
}
