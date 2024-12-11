
package tech.artadevs.finances.services;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.artadevs.finances.controller.UserController;
import tech.artadevs.finances.dtos.UserRegisterRequestDto;
import tech.artadevs.finances.dtos.UserResponseDto;
import tech.artadevs.finances.dtos.ValueAlreadyInUseResponseDto;
import tech.artadevs.finances.exception.ResourceConflictException;
import tech.artadevs.finances.models.User;
import tech.artadevs.finances.repositories.UserRepository;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authenticationService;

    public ValueAlreadyInUseResponseDto checkEmail(String email) {
        Optional<User> existingUserOpt = userRepository.findByEmail(email);

        return new ValueAlreadyInUseResponseDto().setAlreadyInUse(existingUserOpt.isPresent());
    }

    public ValueAlreadyInUseResponseDto checkAccountNumber(Long accountNumber) {
        Optional<User> existingUserOpt = userRepository.findByAccountNumber(accountNumber);

        return new ValueAlreadyInUseResponseDto().setAlreadyInUse(existingUserOpt.isPresent());

    }

    public UserResponseDto signup(UserRegisterRequestDto userDTO) {
        logger.info("Signup request: {} {}", userDTO.getName(), userDTO.getEmail());

        validateEmailNotAlreadyInUse(userDTO);
        validateAccountNumberNotAlreadyInUse(userDTO);

        User newUser = new User()
                .setName(userDTO.getName())
                .setAge(userDTO.getAge())
                .setEmail(userDTO.getEmail())
                .setAccountNumber(userDTO.getAccountNumber())
                .setPassword(authenticationService.encodePassword(userDTO.getPassword()));

        newUser = userRepository.save(newUser);
        logger.info("Sign up completed for {}", newUser.getEmail());
        return getUserResponseDto(newUser);
    }

    private void validateAccountNumberNotAlreadyInUse(UserRegisterRequestDto userDTO) {
        if (checkAccountNumber(userDTO.getAccountNumber()).isAlreadyInUse()) {
            String errorMessage = "Account number already in use: " + userDTO.getAccountNumber();
            logger.error(errorMessage);
            throw new ResourceConflictException(errorMessage);
        }
    }

    private void validateEmailNotAlreadyInUse(UserRegisterRequestDto userDTO) {
        if (checkEmail(userDTO.getEmail()).isAlreadyInUse()) {
            String errorMessage = "Email already in use: " + userDTO.getEmail();
            logger.error(errorMessage);
            throw new ResourceConflictException(errorMessage);
        }
    }

    public UserResponseDto getUserResponseDto(User user) {
        UserResponseDto responseDto = new UserResponseDto()
                .setId(user.getId())
                .setName(user.getName())
                .setAge(user.getAge())
                .setEmail(user.getEmail())
                .setAccountNumber(user.getAccountNumber())
                .setCreatedAt(user.getCreatedAt());
        return responseDto;
    }

    public UserResponseDto updateCurrentUser(UserRegisterRequestDto updatedUser) {
        logger.info("Update request: {} {}", updatedUser.getName(), updatedUser.getEmail());
        User currentUser = authenticationService.getCurrentUser();

        if (!currentUser.getEmail().equalsIgnoreCase(updatedUser.getEmail())) {
            validateEmailNotAlreadyInUse(updatedUser);
        }
        if (currentUser.getAccountNumber() != updatedUser.getAccountNumber()) {
            validateAccountNumberNotAlreadyInUse(updatedUser);
        }

        currentUser
                .setName(updatedUser.getName())
                .setAge(updatedUser.getAge())
                .setEmail(updatedUser.getEmail())
                .setAccountNumber(updatedUser.getAccountNumber())
                .setPassword(authenticationService.encodePassword(updatedUser.getPassword()));
        return getUserResponseDto(userRepository.save(currentUser));
    }

    public void deleteAuthenticatedUser() {
        User currentUser = authenticationService.getCurrentUser();
        logger.info("Delete request: {} {}", currentUser.getName(), currentUser.getEmail());

        currentUser.setDeletedAt(new Date());
        currentUser.setEnabled(false);
        userRepository.save(currentUser);
    }
}
