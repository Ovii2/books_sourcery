package org.example.company.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book_rating")
public class BookRating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "book_id", insertable = false, updatable = false)
    private UUID bookId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "rating")
    private Double rating;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
