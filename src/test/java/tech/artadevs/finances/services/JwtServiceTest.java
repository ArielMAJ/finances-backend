package tech.artadevs.finances.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.jsonwebtoken.ExpiredJwtException;
import tech.artadevs.finances.models.User;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String SECRET_KEY = "7cafe70f72be6ac77d2772b7b267c2da11e7f7087b77bb17c6c517786787b77";
    private static final long JWT_EXPIRATION = 1500L;

    private JwtService jwtService;

    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        Field secretKeyField = JwtService.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtService, SECRET_KEY);

        Field jwtExpirationField = JwtService.class.getDeclaredField("jwtExpiration");
        jwtExpirationField.setAccessible(true);
        jwtExpirationField.set(jwtService, JWT_EXPIRATION);

        lenient().when(mockUser.getUsername()).thenReturn("defaultUser");
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
        assertEquals(jwtService.getExpirationTime(), JWT_EXPIRATION);
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
        Thread.sleep(JWT_EXPIRATION + 200);
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, mockUser));
    }

    @Test
    void testIsTokenValid_expiredToken_invalidUser_returnsInvalid() throws InterruptedException {
        String token = jwtService.generateToken(mockUser);

        Thread.sleep(JWT_EXPIRATION + 200);
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, mockUser));
    }
}
