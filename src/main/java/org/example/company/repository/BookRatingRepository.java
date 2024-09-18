package org.example.company.repository;

import org.example.company.model.BookRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRatingRepository extends JpaRepository<BookRating, UUID> {

    Optional<BookRating> findBookRatingByUserIdAndBookId(UUID bookId, UUID userId);

    List<BookRating> findBookRatingsByBookId(UUID bookId);
}
