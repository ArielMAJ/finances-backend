package tech.artadevs.finances.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import tech.artadevs.finances.dtos.ApiErrorDto;
import tech.artadevs.finances.dtos.FinancialTransactionRequestDto;
import tech.artadevs.finances.dtos.FinancialTransactionResponseDto;
import tech.artadevs.finances.dtos.UserLoginRequestDto;
import tech.artadevs.finances.dtos.UserLoginResponseDto;
import tech.artadevs.finances.dtos.UserRegisterRequestDto;
import tech.artadevs.finances.dtos.UserResponseDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FinancialTransactionControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String userEmail;
    private String userPassword;
    private String userName;
    private Long accountNumber;
    private Integer age;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        userEmail = "user@example.com";
        userPassword = "password";
        userName = "Example User";
        accountNumber = 123456789L;
        age = 26;

        UserRegisterRequestDto signupRequest = new UserRegisterRequestDto()
                .setEmail(userEmail)
                .setPassword(userPassword)
                .setName(userName)
                .setAccountNumber(accountNumber)
                .setAge(age);

        restTemplate.postForEntity("/user/signup", signupRequest, UserResponseDto.class).getBody();

        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + loginAndGetToken(userEmail, userPassword));
    }

    private String loginAndGetToken(String email, String password) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UserLoginRequestDto loginPayload = new UserLoginRequestDto()
                .setEmail(email)
                .setPassword(password);

        HttpEntity<UserLoginRequestDto> entity = new HttpEntity<>(loginPayload, headers);

        ResponseEntity<UserLoginResponseDto> response = restTemplate.exchange("/auth/login", HttpMethod.POST, entity,
                UserLoginResponseDto.class);

        @SuppressWarnings("null")
        String token = response.getBody().getToken();

        return token;
    }

    @Test
    void testCreateTransaction() {
        FinancialTransactionRequestDto request = new FinancialTransactionRequestDto()
                .setValue(100.0)
                .setDescription("Test Transaction");

        HttpEntity<FinancialTransactionRequestDto> entity = new HttpEntity<>(request, headers);

        ResponseEntity<FinancialTransactionResponseDto> response = restTemplate.postForEntity(
                "/user/me/transactions", entity, FinancialTransactionResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        FinancialTransactionResponseDto transaction = response.getBody();
        assertNotNull(transaction);
        assertEquals(100.0, transaction.getValue());
        assertEquals("Test Transaction", transaction.getDescription());
    }

    @Test
    void testUpdateTransaction() {

        FinancialTransactionRequestDto createRequest = new FinancialTransactionRequestDto()
                .setValue(100.0)
                .setDescription("Initial Transaction");
        HttpEntity<FinancialTransactionRequestDto> createEntity = new HttpEntity<>(createRequest, headers);
        ResponseEntity<FinancialTransactionResponseDto> createResponse = restTemplate.postForEntity(
                "/user/me/transactions", createEntity, FinancialTransactionResponseDto.class);

        FinancialTransactionResponseDto financialTransactionResponseDto = createResponse.getBody();
        assertNotNull(financialTransactionResponseDto);
        Long transactionId = financialTransactionResponseDto.getId();

        FinancialTransactionRequestDto updateRequest = new FinancialTransactionRequestDto()
                .setValue(200.0)
                .setDescription("Updated Transaction");
        HttpEntity<FinancialTransactionRequestDto> updateEntity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<Void> updateResponse = restTemplate.exchange(
                "/user/me/transactions/" + transactionId, HttpMethod.PUT, updateEntity, Void.class);

        assertEquals(HttpStatus.ACCEPTED, updateResponse.getStatusCode());

        ResponseEntity<FinancialTransactionResponseDto> getResponse = restTemplate.exchange(
                "/user/me/transactions/" + transactionId, HttpMethod.GET, new HttpEntity<>(headers),
                FinancialTransactionResponseDto.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        FinancialTransactionResponseDto updatedTransaction = getResponse.getBody();
        assertNotNull(updatedTransaction);
        assertEquals(200.0, updatedTransaction.getValue());
        assertEquals("Updated Transaction", updatedTransaction.getDescription());
    }

    @Test
    void testGetTransactionByIdWhenTransactionExists() {

        FinancialTransactionRequestDto createRequest = new FinancialTransactionRequestDto()
                .setValue(50.0)
                .setDescription("Get Test Transaction");
        HttpEntity<FinancialTransactionRequestDto> createEntity = new HttpEntity<>(createRequest, headers);
        ResponseEntity<FinancialTransactionResponseDto> createResponse = restTemplate.postForEntity(
                "/user/me/transactions", createEntity, FinancialTransactionResponseDto.class);

        FinancialTransactionResponseDto financialTransactionResponseDto = createResponse.getBody();
        assertNotNull(financialTransactionResponseDto);
        Long transactionId = financialTransactionResponseDto.getId();

        ResponseEntity<FinancialTransactionResponseDto> getResponse = restTemplate.exchange(
                "/user/me/transactions/" + transactionId, HttpMethod.GET, new HttpEntity<>(headers),
                FinancialTransactionResponseDto.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        FinancialTransactionResponseDto transaction = getResponse.getBody();
        assertNotNull(transaction);
        assertEquals(50.0, transaction.getValue());
        assertEquals("Get Test Transaction", transaction.getDescription());
    }


    @Test
    void testGetTransactionByIdWhenNoTransactionExists() {
        ResponseEntity<ApiErrorDto> getResponse = restTemplate.exchange(
                "/user/me/transactions/1", HttpMethod.GET, new HttpEntity<>(headers),
                ApiErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
        ApiErrorDto apiError = getResponse.getBody();
        assertNotNull(apiError);
        assertEquals("Financial Transaction not found.", apiError.getDetail());
    }

    @Test
    void testListAllTransactions() {

        for (int i = 1; i <= 3; i++) {
            FinancialTransactionRequestDto request = new FinancialTransactionRequestDto()
                    .setValue(10.0 * i)
                    .setDescription("Transaction " + i);
            HttpEntity<FinancialTransactionRequestDto> entity = new HttpEntity<>(request, headers);
            ResponseEntity<FinancialTransactionResponseDto> createResponse = restTemplate.postForEntity(
                    "/user/me/transactions", entity, FinancialTransactionResponseDto.class);

            assertEquals(HttpStatus.OK, createResponse.getStatusCode(), "Transaction creation failed for index: " + i);
            assertNotNull(createResponse.getBody(), "Response body is null for transaction index: " + i);
        }

        ResponseEntity<FinancialTransactionResponseDto[]> response = restTemplate.exchange(
                "/user/me/transactions", HttpMethod.GET, new HttpEntity<>(headers),
                FinancialTransactionResponseDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to fetch transactions");
        FinancialTransactionResponseDto[] transactions = response.getBody();
        assertNotNull(transactions, "Transaction list is null");
        assertTrue(transactions.length >= 3, "Expected at least 3 transactions, found: " + transactions.length);
    }

    @Test
    void testCalculateTotalTransactionsValue() {

        for (int i = 1; i <= 3; i++) {
            FinancialTransactionRequestDto request = new FinancialTransactionRequestDto()
                    .setValue(10.0 * i)
                    .setDescription("Transaction " + i);
            HttpEntity<FinancialTransactionRequestDto> entity = new HttpEntity<>(request, headers);
            restTemplate.postForEntity("/user/me/transactions", entity, FinancialTransactionResponseDto.class);
        }

        ResponseEntity<Double> response = restTemplate.exchange(
                "/user/me/transactions/total", HttpMethod.GET, new HttpEntity<>(headers), Double.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Double totalValue = response.getBody();
        assertNotNull(totalValue);
        assertEquals(60.0, totalValue);
    }

    @Test
    void testDeleteTransaction() {

        FinancialTransactionRequestDto createRequest = new FinancialTransactionRequestDto()
                .setValue(100.0)
                .setDescription("Delete Test Transaction");
        HttpEntity<FinancialTransactionRequestDto> createEntity = new HttpEntity<>(createRequest, headers);
        ResponseEntity<FinancialTransactionResponseDto> createResponse = restTemplate.postForEntity(
                "/user/me/transactions", createEntity, FinancialTransactionResponseDto.class);

        FinancialTransactionResponseDto financialTransactionResponseDto = createResponse.getBody();
        assertNotNull(financialTransactionResponseDto);
        Long transactionId = financialTransactionResponseDto.getId();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/user/me/transactions/" + transactionId, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<FinancialTransactionResponseDto[]> getResponse = restTemplate.exchange(
                "/user/me/transactions", HttpMethod.GET, new HttpEntity<>(headers),
                FinancialTransactionResponseDto[].class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        FinancialTransactionResponseDto[] transactions = getResponse.getBody();
        assertNotNull(transactions);
        assertTrue(transactions.length == 0);
    }
}
