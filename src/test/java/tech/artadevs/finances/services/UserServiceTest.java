package tech.artadevs.finances.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tech.artadevs.finances.dtos.UserRegisterRequestDto;
import tech.artadevs.finances.dtos.UserResponseDto;
import tech.artadevs.finances.dtos.ValueAlreadyInUseResponseDto;
import tech.artadevs.finances.exception.ResourceConflictException;
import tech.artadevs.finances.models.User;
import tech.artadevs.finances.repositories.UserRepository;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkEmail_ShouldReturnAlreadyInUse_WhenEmailExists() {
        String email = "existing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        ValueAlreadyInUseResponseDto response = userService.checkEmail(email);

        assertTrue(response.isAlreadyInUse());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void checkEmail_ShouldReturnNotInUse_WhenEmailDoesNotExist() {
        String email = "new@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ValueAlreadyInUseResponseDto response = userService.checkEmail(email);

        assertFalse(response.isAlreadyInUse());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void checkAccountNumber_ShouldReturnAlreadyInUse_WhenAccountNumberExists() {
        Long accountNumber = 123456L;
        when(userRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(new User()));

        ValueAlreadyInUseResponseDto response = userService.checkAccountNumber(accountNumber);

        assertTrue(response.isAlreadyInUse());
        verify(userRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void checkAccountNumber_ShouldReturnNotInUse_WhenAccountNumberDoesNotExist() {
        Long accountNumber = 123456L;
        when(userRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        ValueAlreadyInUseResponseDto response = userService.checkAccountNumber(accountNumber);

        assertFalse(response.isAlreadyInUse());
        verify(userRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void signup_ShouldCreateAndReturnNewUser_WhenValidDataProvided() {
        UserRegisterRequestDto userDTO = new UserRegisterRequestDto()
                .setName("John Doe")
                .setAge(30)
                .setEmail("john.doe@example.com")
                .setAccountNumber(123456L)
                .setPassword("password123");

        User newUser = new User()
                .setName(userDTO.getName())
                .setAge(userDTO.getAge())
                .setEmail(userDTO.getEmail())
                .setAccountNumber(userDTO.getAccountNumber())
                .setPassword("encodedPassword");

        when(authenticationService.encodePassword(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        UserResponseDto response = userService.signup(userDTO);

        assertEquals(userDTO.getName(), response.getName());
        assertEquals(userDTO.getEmail(), response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_ShouldThrowException_WhenEmailAlreadyExists() {
        UserRegisterRequestDto userDTO = new UserRegisterRequestDto()
                .setEmail("existing@example.com");

        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(ResourceConflictException.class, () -> userService.signup(userDTO));
        verify(userRepository).findByEmail(userDTO.getEmail());
    }

    @Test
    void signup_ShouldThrowException_WhenAccountNumberAlreadyExists() {
        UserRegisterRequestDto userDTO = new UserRegisterRequestDto()
                .setAccountNumber(123456L);

        when(userRepository.findByAccountNumber(userDTO.getAccountNumber())).thenReturn(Optional.of(new User()));

        assertThrows(ResourceConflictException.class, () -> userService.signup(userDTO));
        verify(userRepository).findByAccountNumber(userDTO.getAccountNumber());
    }

    @Test
    void updateCurrentUser_ShouldUpdateAndReturnUser_WhenValidDataProvided_DifferentEmail_DifferentAccountNumber() {
        UserRegisterRequestDto updatedUser = new UserRegisterRequestDto()
                .setName("Updated Name")
                .setAge(35)
                .setEmail("updated.email@example.com")
                .setAccountNumber(987654L)
                .setPassword("newPassword123");

        User currentUser = new User()
                .setName("Old Name")
                .setAge(30)
                .setEmail("old.email@example.com")
                .setAccountNumber(123456L)
                .setPassword("oldPassword");

        User updatedSavedUser = new User()
                .setName(updatedUser.getName())
                .setAge(updatedUser.getAge())
                .setEmail(updatedUser.getEmail())
                .setAccountNumber(updatedUser.getAccountNumber())
                .setPassword("encodedNewPassword");

        when(authenticationService.getCurrentUser()).thenReturn(currentUser);
        when(authenticationService.encodePassword(updatedUser.getPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedSavedUser);

        UserResponseDto response = userService.updateCurrentUser(updatedUser);

        assertEquals(updatedUser.getName(), response.getName());
        assertEquals(updatedUser.getEmail(), response.getEmail());
        verify(userRepository).save(currentUser);
    }

    @Test
    void updateCurrentUser_ShouldUpdateAndReturnUser_WhenValidDataProvided_SameEmail_SameAccountNumber() {
        UserRegisterRequestDto updatedUser = new UserRegisterRequestDto()
                .setName("Updated Name")
                .setAge(35)
                .setEmail("email@example.com")
                .setAccountNumber(123456L)
                .setPassword("newPassword123");

        User currentUser = new User()
                .setName("Old Name")
                .setAge(30)
                .setEmail("email@example.com")
                .setAccountNumber(123456L)
                .setPassword("oldPassword");

        User updatedSavedUser = new User()
                .setName(updatedUser.getName())
                .setAge(updatedUser.getAge())
                .setEmail(updatedUser.getEmail())
                .setAccountNumber(updatedUser.getAccountNumber())
                .setPassword("encodedNewPassword");

        when(authenticationService.getCurrentUser()).thenReturn(currentUser);
        when(authenticationService.encodePassword(updatedUser.getPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedSavedUser);

        UserResponseDto response = userService.updateCurrentUser(updatedUser);

        assertEquals(updatedUser.getName(), response.getName());
        assertEquals(updatedUser.getEmail(), response.getEmail());
        verify(userRepository).save(currentUser);
    }

    @Test
    void deleteAuthenticatedUser_ShouldMarkUserAsDeleted() {
        User currentUser = new User()
                .setName("John Doe")
                .setEmail("john.doe@example.com")
                .setEnabled(true);

        when(authenticationService.getCurrentUser()).thenReturn(currentUser);

        userService.deleteAuthenticatedUser();

        assertNotNull(currentUser.getDeletedAt());
        assertFalse(currentUser.getEnabled());
        verify(userRepository).save(currentUser);
    }
}
