package org.example.company.dto.register;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class RegisterResponseDTO {

    private UUID id;
    private String username;
    private String email;
    private String token;
    private String message;

    public RegisterResponseDTO(String message) {
        this.message = message;
    }

    public RegisterResponseDTO(UUID id, String message) {
        this.id = id;
        this.message = message;
    }
}
