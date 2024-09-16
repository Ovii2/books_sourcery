package org.example.company.service;

import lombok.RequiredArgsConstructor;
import org.example.company.dto.register.RegisterRequestDTO;
import org.example.company.dto.register.RegisterResponseDTO;
import org.example.company.enums.UserRoles;
import org.example.company.exception.UserAlreadyExistsException;
import org.example.company.model.User;
import org.example.company.repository.UserRepository;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final Argon2PasswordEncoder passwordEncoder;

    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) throws UserAlreadyExistsException {
        if (userRepository.existsUserByEmail(registerRequestDTO.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists!");
        }
        if (userRepository.existsUserByUsername(registerRequestDTO.getUsername())) {
            throw new UserAlreadyExistsException("This username already exists!");
        }

        validateUsername(registerRequestDTO.getUsername());
        validateEmail(registerRequestDTO.getEmail());
        validatePassword(registerRequestDTO.getPassword());


        User user = User.builder()
                .username(registerRequestDTO.getUsername())
                .email(registerRequestDTO.getEmail())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .role(UserRoles.ROLE_USER)
                .build();
        userRepository.save(user);
        return new RegisterResponseDTO(user.getId(), "User registered successfully!");
    }

    public void validateEmail(String email) {
        if (!Pattern.matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", email)) {
            throw new IllegalArgumentException("Invalid email address format");
        }
    }

    public void validatePassword(String password) {
        int minLength = 8;

        if (password == null || password.isEmpty() || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty or blank");
        }
        if (!Pattern.matches(".*[A-Z].*", password)) {
            throw new IllegalArgumentException("Password must include at least one uppercase letter");
        }
        if (!Pattern.matches(".*[a-z].*", password)) {
            throw new IllegalArgumentException("Password must include at least one lowercase letter");
        }
        if (!Pattern.matches(".*\\d.*", password)) {
            throw new IllegalArgumentException("Password must include at least one number");
        }
        if (password.length() < minLength) {
            throw new IllegalArgumentException(String.format("Password must be at least %d characters long %n", minLength));
        }
    }

    public void validateUsername(String userName) throws IllegalArgumentException {
        int min = 4;
        int max = 20;

        if (userName == null || userName.isEmpty() || userName.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty!");
        }
        if (userName.length() < min) {
            throw new IllegalArgumentException("Username must be at least 4 characters");
        }
        if (userName.length() > max) {
            throw new IllegalArgumentException("Username cannot be longer than 20 characters");
        }
        if (userName.startsWith(" ") || userName.endsWith(" ")) {
            throw new IllegalArgumentException("Username cannot start or end with a space");
        }
    }
}
