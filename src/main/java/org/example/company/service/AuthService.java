package org.example.company.service;

import lombok.RequiredArgsConstructor;
import org.example.company.dto.login.LoginRequestDTO;
import org.example.company.dto.login.LoginResponseDTO;
import org.example.company.dto.register.RegisterRequestDTO;
import org.example.company.dto.register.RegisterResponseDTO;
import org.example.company.enums.UserRoles;
import org.example.company.exception.UserAlreadyExistsException;
import org.example.company.model.User;
import org.example.company.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final Argon2PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;


    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) throws UserAlreadyExistsException {
        if (userRepository.existsUserByEmail(registerRequestDTO.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists!");
        }
        if (userRepository.existsUserByUsername(registerRequestDTO.getUsername())) {
            throw new UserAlreadyExistsException("This username already exists!");
        }

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

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));

        User user = userRepository.findUserByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwtToken = jwtService.generateToken(user);
        tokenService.revokeAllUserTokens(user);
        tokenService.saveUserToken(user, jwtToken);

        return LoginResponseDTO.builder()
                .token(jwtToken)
                .message("User logged in successfully")
                .build();
    }


    public void validatePassword(String password) {
        int minLength = 8;

        if (password == null || password.isEmpty() || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty or blank");
        }
        if (password.length() < minLength) {
            throw new IllegalArgumentException(String.format("Password must be at least %d characters long", minLength));
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
    }


    public Optional<User> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findUserByUsername(username);
        }
        return Optional.empty();
    }
}
