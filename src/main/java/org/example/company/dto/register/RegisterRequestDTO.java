package org.example.company.dto.register;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.company.enums.UserRoles;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {

    @NotNull(message = "Username is mandatory")
    @NotEmpty(message = "Username cannot be empty")
    @NotBlank(message = "Username cannot start or end with a space")
    @Size(min = 4, max = 20, message = "Username must be between {min} and {max} characters long")
    private String username;

    @Email(message = "Email is not valid",
            regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least {min} characters long")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserRoles role;

    public RegisterRequestDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
