package org.example.company.controller;

import lombok.RequiredArgsConstructor;
import org.example.company.dto.login.LoginRequestDTO;
import org.example.company.dto.login.LoginResponseDTO;
import org.example.company.dto.register.RegisterRequestDTO;
import org.example.company.dto.register.RegisterResponseDTO;
import org.example.company.exception.UserAlreadyExistsException;
import org.example.company.exception.UserNotFoundException;
import org.example.company.exception.UsernameOrPasswordInvalidException;
import org.example.company.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
        try {
            RegisterResponseDTO response = authService.register(registerRequestDTO);
            return ResponseEntity.ok(response);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new RegisterResponseDTO(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RegisterResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            LoginResponseDTO loggedUser = authService.login(loginRequestDTO);
            return ResponseEntity.ok(loggedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LoginResponseDTO(e.getMessage()));
        } catch (UsernameOrPasswordInvalidException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponseDTO(e.getMessage()));
        }
    }
}
