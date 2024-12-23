
package tech.artadevs.finances.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import tech.artadevs.finances.dtos.UserRegisterRequestDto;
import tech.artadevs.finances.dtos.UserResponseDto;
import tech.artadevs.finances.dtos.ValueAlreadyInUseResponseDto;
import tech.artadevs.finances.services.AuthenticationService;
import tech.artadevs.finances.services.UserService;

@Validated
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signup")
    public UserResponseDto signup(@Valid @RequestBody UserRegisterRequestDto user) {
        return userService.signup(user);
    }

    @PutMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateCurrentUser(@Valid @RequestBody UserRegisterRequestDto updatedUser) {
        return userService.updateCurrentUser(updatedUser);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public void deleteSelf() {
        userService.deleteAuthenticatedUser();
    }

    @GetMapping("/check-email/{email}")
    public ValueAlreadyInUseResponseDto findByEmail(@PathVariable String email) {
        return userService.checkEmail(email);
    }

    @GetMapping("/check-account-number/{accountNumber}")
    public ValueAlreadyInUseResponseDto findByAccountNumber(@PathVariable Long accountNumber) {
        return userService.checkAccountNumber(accountNumber);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public UserResponseDto authenticatedUser() {
        return userService.getUserResponseDto(authenticationService.getCurrentUser());
    }
}
