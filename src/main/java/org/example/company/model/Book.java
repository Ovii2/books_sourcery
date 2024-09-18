package org.example.company.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.List;
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
    @Max(value = 5, message = "Rating must not be larger than 5")
    @Column(name = "rating", nullable = false)
    private Double rating = 0.0;

    @Column(name = "rating_count", nullable = false)
    private Long ratingCount;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookRating> ratings;

    public void addRating(BookRating bookRating) {
        ratings.add(bookRating);
        bookRating.setBook(this);
        updateRating();
    }

    public void removeRating(BookRating bookRating) {
        ratings.remove(bookRating);
        bookRating.setBook(null);
        updateRating();
    }

    private void updateRating() {
        if (!ratings.isEmpty()) {
            this.rating = ratings.stream()
                    .mapToDouble(BookRating::getRating)
                    .average()
                    .orElse(0.0);
        } else {
            this.rating = 0.0;
            this.ratingCount = 0L;
        }
    }
}
