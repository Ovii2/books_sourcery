package org.example.company.dto.book;

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
public class BookResponseDTO {

    private UUID id;
    private String title;
    private String message;

    public BookResponseDTO(String message) {
        this.message = message;
    }
}
