package org.example.company.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "year", nullable = false)
    private Integer year;

    @PositiveOrZero(message = "Rating must be zero or a positive number")
    @Max(value = 5, message = "Raring must not be larger than 5")
    @Column(name = "rating", nullable = false)
    private Double rating;
}
