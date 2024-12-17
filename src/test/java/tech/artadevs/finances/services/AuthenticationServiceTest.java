package tech.artadevs.finances.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import tech.artadevs.finances.dtos.UserLoginRequestDto;
import tech.artadevs.finances.exception.ResourceNotFoundException;
import tech.artadevs.finances.models.User;
import tech.artadevs.finances.repositories.UserRepository;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void encodePassword_ShouldReturnEncodedPassword() {
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        String result = authenticationService.encodePassword(rawPassword);

        assertEquals(encodedPassword, result);
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void authenticate_ShouldReturnUser_WhenCredentialsAreValid() {
        String email = "test@example.com";
        String password = "password123";
        User user = new User()
                .setEmail(email);

        UserLoginRequestDto loginRequest = new UserLoginRequestDto()
                .setEmail(email)
                .setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        User result = authenticationService.authenticate(loginRequest);

        assertEquals(user, result);
        verify(userRepository).findByEmail(email);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        String email = "notfound@example.com";
        String password = "password123";
        UserLoginRequestDto loginRequest = new UserLoginRequestDto()
                .setEmail(email)
                .setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authenticationService.authenticate(loginRequest));
        verify(userRepository).findByEmail(email);
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void getCurrentUser_ShouldReturnAuthenticatedUser() {
        User user = new User()
                .setEnabled(true);

        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User result = authenticationService.getCurrentUser();

        assertEquals(user, result);
        verify(securityContext).getAuthentication();
    }

    @Test
    void getCurrentUser_ShouldThrowBadCredentialsException_WhenUserIsDeletedOrDisabled() {
        User user = new User()
                .setDeletedAt(new Date())
                .setEnabled(true);

        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(BadCredentialsException.class, () -> authenticationService.getCurrentUser());
    }

    @Test
    void getCurrentUser_ShouldThrowBadCredentialsException_WhenUserIsDisabled() {
        User user = new User()
                .setDeletedAt(null)
                .setEnabled(false);

        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(BadCredentialsException.class, () -> authenticationService.getCurrentUser());
    }
}
