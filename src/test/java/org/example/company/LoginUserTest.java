package org.example.company;

import org.example.company.dto.login.LoginRequestDTO;
import org.example.company.dto.login.LoginResponseDTO;
import org.example.company.model.User;
import org.example.company.repository.UserRepository;
import org.example.company.service.AuthService;
import org.example.company.service.JwtService;
import org.example.company.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("testpassword")
                .email("test@example.com")
                .build();
    }

    @Test
    void login_successful() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("testuser", "testpassword");
        String token = "sampleJwtToken";

        when(userRepository.findUserByUsername(loginRequestDTO.getUsername()))
                .thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(token);

        LoginResponseDTO responseDTO = authService.login(loginRequestDTO);


        assertNotNull(responseDTO);
        assertEquals(token, responseDTO.getToken());
        assertEquals("User logged in successfully", responseDTO.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).revokeAllUserTokens(user);
        verify(tokenService).saveUserToken(user, token);
    }

    @Test
    void login_userNotFound() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("nonExistentUser", "password");

        when(userRepository.findUserByUsername("nonExistentUser"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.login(loginRequestDTO));

        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_invalidCredentials() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("testuser", "wrongpassword");


        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));


        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequestDTO));
        verify(jwtService, never()).generateToken(any());
    }
}
