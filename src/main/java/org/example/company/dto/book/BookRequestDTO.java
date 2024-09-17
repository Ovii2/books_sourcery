package org.example.company.dto.book;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDTO {

    private String title;
    private String author;
    private Integer year;

}
