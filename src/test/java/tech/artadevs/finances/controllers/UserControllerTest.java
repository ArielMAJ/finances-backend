package tech.artadevs.finances.controllers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.hibernate.mapping.Array;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import tech.artadevs.finances.dtos.UserLoginRequestDto;
import tech.artadevs.finances.dtos.UserLoginResponseDto;
import tech.artadevs.finances.dtos.UserRegisterRequestDto;
import tech.artadevs.finances.dtos.UserResponseDto;
import tech.artadevs.finances.dtos.ValueAlreadyInUseResponseDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String userEmail;
    private String userPassword;
    private String userName;
    private Long accountNumber;
    private Integer age;
    private HttpHeaders headers;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

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
    void testSignupReturnsSuccess() {
        String testUserEmail = "test_" + userEmail;
        UserRegisterRequestDto signupRequest = new UserRegisterRequestDto()
                .setEmail(testUserEmail)
                .setPassword("test_" + userPassword)
                .setName("test_" + userName)
                .setAccountNumber(accountNumber + 1L)
                .setAge(age + 1);

        ResponseEntity<UserResponseDto> response = restTemplate.postForEntity("/user/signup", signupRequest,
                UserResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserResponseDto userResponse = response.getBody();
        assertNotNull(userResponse);
        assertEquals(testUserEmail, userResponse.getEmail());
    }

    @Test
    void testSignupReturnsAlreadyRegisteredEmail() {
        UserRegisterRequestDto signupRequest = new UserRegisterRequestDto()
                .setEmail(userEmail)
                .setPassword("test_" + userPassword)
                .setName("test_" + userName)
                .setAccountNumber(accountNumber + 1L)
                .setAge(age + 1);

        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity("/user/signup", signupRequest,
                ApiErrorDto.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ApiErrorDto errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Email already in use: " + userEmail, errorResponse.getDetail());
    }

    @Test
    void testSignupReturnsAlreadyRegisteredAccountNumber() {
        String testUserEmail = "test_" + userEmail;
        UserRegisterRequestDto signupRequest = new UserRegisterRequestDto()
                .setEmail(testUserEmail)
                .setPassword("test_" + userPassword)
                .setName("test_" + userName)
                .setAccountNumber(accountNumber)
                .setAge(age + 1);

        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity("/user/signup", signupRequest,
                ApiErrorDto.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ApiErrorDto errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Account number already in use: " + accountNumber, errorResponse.getDetail());
    }

    @Test
    void testSignupReturnsConstraintViolation() {
        String testUserEmail = "test_" + userEmail;
        UserRegisterRequestDto signupRequest = new UserRegisterRequestDto()
                .setEmail(testUserEmail)
                .setPassword("0")
                .setName("a")
                .setAccountNumber(0L)
                .setAge(1);

        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, List<String>>> response = restTemplate.postForEntity("/user/signup", signupRequest,
                (Class<Map<String, List<String>>>) (Class<?>) Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, List<String>> errorResponse = response.getBody();
        assertNotNull(errorResponse);

        List<String> errors = errorResponse.get("detail");
        assertNotNull(errors);
        assertTrue(errors.size() > 0, "Expected validation errors, but got none.");

        assertTrue(errors.contains("The length of full name must be between 2 and 100 characters."));
        assertTrue(errors.contains("The length of full name must be between 2 and 100 characters."));
        assertTrue(errors.contains("Account number should be greater than zero."));
        assertTrue(errors.contains("Age must be at least 18."));
    }

    @Test
    void testUpdateCurrentUser() {
        UserRegisterRequestDto updatedUser = new UserRegisterRequestDto()
                .setEmail(userEmail)
                .setPassword(userPassword)
                .setName("Updated User")
                .setAccountNumber(accountNumber)
                .setAge(age + 10);

        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + loginAndGetToken(userEmail, userPassword));

        HttpEntity<UserRegisterRequestDto> entity = new HttpEntity<>(updatedUser, headers);
        ResponseEntity<UserResponseDto> update_response = restTemplate.exchange("/user/me", HttpMethod.PUT,
                entity, UserResponseDto.class);

        assertEquals(HttpStatus.OK, update_response.getStatusCode());
        UserResponseDto updatedUserResponse = update_response.getBody();
        assertNotNull(updatedUserResponse);
        assertEquals("Updated User", updatedUserResponse.getName());
        assertEquals(age + 10, updatedUserResponse.getAge());
    }

    @Test
    void testDeleteCurrentUser() {
        ResponseEntity<Void> delete_response = restTemplate.exchange("/user/me", HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, delete_response.getStatusCode());

        ResponseEntity<UserResponseDto> get_response = restTemplate.exchange("/user/me", HttpMethod.GET,
                new HttpEntity<>(headers),
                UserResponseDto.class);

        assertEquals(HttpStatus.UNAUTHORIZED, get_response.getStatusCode());
    }

    @Test
    void testCheckEmailAvailability() {

        ResponseEntity<ValueAlreadyInUseResponseDto> response = restTemplate.exchange("/user/check-email/" + userEmail,
                HttpMethod.GET, null, ValueAlreadyInUseResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ValueAlreadyInUseResponseDto valueAlreadyInUseResponse = response.getBody();
        assertNotNull(valueAlreadyInUseResponse);
        assertEquals(true, valueAlreadyInUseResponse.isAlreadyInUse());
    }

    @Test
    void testCheckAccountNumberAvailability() {

        ResponseEntity<ValueAlreadyInUseResponseDto> response = restTemplate.exchange(
                "/user/check-account-number/" + accountNumber,
                HttpMethod.GET, null, ValueAlreadyInUseResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ValueAlreadyInUseResponseDto valueAlreadyInUseResponse = response.getBody();
        assertNotNull(valueAlreadyInUseResponse);
        assertEquals(true, valueAlreadyInUseResponse.isAlreadyInUse());
    }

    @Test
    void testGetAuthenticatedUser() {
        ResponseEntity<UserResponseDto> response = restTemplate.exchange("/user/me", HttpMethod.GET,
                new HttpEntity<>(headers),
                UserResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserResponseDto authenticatedUser = response.getBody();
        assertNotNull(authenticatedUser);
        assertEquals(userEmail, authenticatedUser.getEmail());
    }


    @Test
    void testGetAuthenticatedUserUnauthorized() throws InterruptedException {
        Thread.sleep(jwtExpiration + 200);
        ResponseEntity<ApiErrorDto> response = restTemplate.exchange("/user/me", HttpMethod.GET,
                new HttpEntity<>(headers),
                ApiErrorDto.class);

                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ApiErrorDto errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Invalid or expired JWT token.", errorResponse.getDetail());
    }
}
