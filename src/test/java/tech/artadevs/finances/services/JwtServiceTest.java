package tech.artadevs.finances.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

import io.jsonwebtoken.ExpiredJwtException;
import tech.artadevs.finances.models.User;

@SpringBootTest
@PropertySource("classpath:application.properties")
class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private User mockUser;

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        Field secretKeyField = JwtService.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtService, secretKey);

        Field jwtExpirationField = JwtService.class.getDeclaredField("jwtExpiration");
        jwtExpirationField.setAccessible(true);
        jwtExpirationField.set(jwtService, jwtExpiration);

        when(mockUser.getUsername()).thenReturn("defaultUser");
    }

    @Test
    void testExtractUsername() {
        String token = jwtService.generateToken(mockUser);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(mockUser.getUsername(), extractedUsername);
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(mockUser);
        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(mockUser.getUsername(), extractedUsername);
    }

    @Test
    void testGetExpirationTime() {
        assertEquals(jwtService.getExpirationTime(), jwtExpiration);
    }

    @Test
    void testIsTokenValid_notExpiredToken_validUser_returnsValid() {
        String token = jwtService.generateToken(mockUser);

        boolean isValid = jwtService.isTokenValid(token, mockUser);

        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_notExpiredToken_invalidUser_returnsInvalid() {
        String token = jwtService.generateToken(mockUser);

        when(mockUser.getUsername()).thenReturn("otheruser");
        boolean isValid = jwtService.isTokenValid(token, mockUser);

        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_expiredToken_validUser_returnsInvalid() throws InterruptedException {

        String token = jwtService.generateToken(mockUser);
        Thread.sleep(jwtExpiration + 200);
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, mockUser));
    }

    @Test
    void testIsTokenValid_expiredToken_invalidUser_returnsInvalid() throws InterruptedException {
        String token = jwtService.generateToken(mockUser);

        Thread.sleep(jwtExpiration + 200);
        when(mockUser.getUsername()).thenReturn("otheruser");
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, mockUser));
    }
}
