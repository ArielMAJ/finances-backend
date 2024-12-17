package tech.artadevs.finances.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import tech.artadevs.finances.dtos.ApiErrorDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomErrorControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testErrorHandling() {
        ResponseEntity<ApiErrorDto> response = restTemplate.getForEntity("/error", ApiErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ApiErrorDto apiErrorDto = response.getBody();
        assertNotNull(apiErrorDto);
        assertEquals("Access /docs to see the API documentation.", apiErrorDto.getDetail());
    }
}
