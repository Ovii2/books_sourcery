package org.example.company;

import org.example.company.dto.register.RegisterRequestDTO;
import org.example.company.dto.register.RegisterResponseDTO;
import org.example.company.enums.UserRoles;
import org.example.company.exception.UserAlreadyExistsException;
import org.example.company.model.User;
import org.example.company.repository.UserRepository;
import org.example.company.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Argon2PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_successfulRegistration() {
        RegisterRequestDTO requestDTO = new RegisterRequestDTO("newUser", "newEmail@example.com", "StrongPass1!");

        when(userRepository.existsUserByEmail(requestDTO.getEmail())).thenReturn(false);
        when(userRepository.existsUserByUsername(requestDTO.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(requestDTO.getPassword())).thenReturn("hashedPassword");

        RegisterResponseDTO responseDTO = authService.register(requestDTO);

        verify(userRepository, times(1)).save(any(User.class));
        assertNotNull(responseDTO);
        assertEquals("User registered successfully!", responseDTO.getMessage());
    }

    @Test
    void register_throwsExceptionWhenEmailAlreadyExists() {
        RegisterRequestDTO requestDTO = new RegisterRequestDTO("newUser", "existingEmail@example.com", "StrongPass1!");

        when(userRepository.existsUserByEmail(requestDTO.getEmail())).thenReturn(true);


        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            authService.register(requestDTO);
        });


        assertEquals("Email already exists!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_throwsExceptionWhenUsernameAlreadyExists() {
        RegisterRequestDTO requestDTO = new RegisterRequestDTO("existingUser", "newEmail@example.com", "StrongPass1!");

        when(userRepository.existsUserByUsername(requestDTO.getUsername())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            authService.register(requestDTO);
        });

        assertEquals("This username already exists!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_invalidEmail() {
        RegisterRequestDTO requestDTO = new RegisterRequestDTO("newUser", "invalidEmail", "StrongPass1!");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(requestDTO);
        });

        assertEquals("Invalid email address format", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_invalidPassword() {
        RegisterRequestDTO requestDTO = new RegisterRequestDTO("newUser", "newEmail@example.com", "weak");


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(requestDTO);
        });

        assertEquals("Password must be at least " + 8 + " characters long", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    void register_invalidUsername() {
        RegisterRequestDTO requestDTO = new RegisterRequestDTO("us", "newEmail@example.com", "StrongPass1!");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(requestDTO);
        });

        assertEquals("Username must be at least " + 4 + " characters", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
