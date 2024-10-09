package org.example.company.service;

import lombok.RequiredArgsConstructor;
import org.example.company.dto.login.LoginRequestDTO;
import org.example.company.dto.login.LoginResponseDTO;
import org.example.company.dto.register.RegisterRequestDTO;
import org.example.company.dto.register.RegisterResponseDTO;
import org.example.company.enums.UserRoles;
import org.example.company.exception.UserAlreadyExistsException;
import org.example.company.exception.UserAlreadyLoggedInException;
import org.example.company.model.User;
import org.example.company.repository.TokenRepository;
import org.example.company.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final Argon2PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;


    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) throws UserAlreadyExistsException {
        if (userRepository.existsUserByEmail(registerRequestDTO.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists!");
        }
        if (userRepository.existsUserByUsername(registerRequestDTO.getUsername())) {
            throw new UserAlreadyExistsException("This username already exists!");
        }

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

        var validUserTokens = tokenService.getAllValidUserTokens(user);
        if (!validUserTokens.isEmpty()) {
           throw new UserAlreadyLoggedInException("You are already logged in");
        }

        tokenService.deleteAllUserTokens(user);
        String jwtToken = jwtService.generateToken(user);
        tokenService.saveUserToken(user, jwtToken);

        return LoginResponseDTO.builder()
                .token(jwtToken)
                .message("User logged in successfully")
                .build();
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
