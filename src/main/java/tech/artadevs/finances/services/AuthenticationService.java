package tech.artadevs.finances.services;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tech.artadevs.finances.dtos.UserLoginRequestDto;
import tech.artadevs.finances.exception.ResourceNotFoundException;
import tech.artadevs.finances.models.User;
import tech.artadevs.finances.repositories.UserRepository;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        UserRepository userRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public User authenticate(UserLoginRequestDto input) {
        Optional<User> user = userRepository.findByEmail(input.getEmail());

        if (user.isEmpty())
            throw new ResourceNotFoundException("User Account");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return user.get();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if (currentUser.getDeletedAt() != null || !currentUser.getEnabled())
            throw new BadCredentialsException("User deleted or disabled.");
        return currentUser;
    }
}
