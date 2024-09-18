package org.example.company.service;

import lombok.RequiredArgsConstructor;
import org.example.company.dto.book.BookRequestDTO;
import org.example.company.dto.book.BookResponseDTO;
import org.example.company.dto.bookRating.BookRatingResponseDTO;
import org.example.company.exception.BookNotFoundException;
import org.example.company.exception.NotAdminException;
import org.example.company.model.Book;
import org.example.company.model.BookRating;
import org.example.company.model.User;
import org.example.company.repository.BookRatingRepository;
import org.example.company.repository.BookRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthService authService;
    private final BookRatingRepository bookRatingRepository;


    public List<Book> filterBooks(Optional<String> title, Optional<String> author, Optional<Integer> year, Optional<Double> rating) {
        Specification<Book> spec = Specification.where(null);

        if (title.isPresent()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("title"), "%" + title.get() + "%"));
        }
        if (author.isPresent()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("author"), "%" + author.get() + "%"));
        }
        if (year.isPresent()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("year"), year.get()));
        }
        if (rating.isPresent()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("rating"), rating.get()));
        }

        return bookRepository.findAll(spec);
    }


    public BookResponseDTO createBook(BookRequestDTO bookRequestDTO) {
        if (bookRepository.existsByTitle(bookRequestDTO.getTitle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book with this title already exists");
        }

        Book book = Book.builder()
                .title(bookRequestDTO.getTitle())
                .author(bookRequestDTO.getAuthor())
                .year(bookRequestDTO.getYear())
                .rating(0.00)
                .ratingCount(0L)
                .build();

        bookRepository.save(book);
        return new BookResponseDTO("Book created successfully");
    }


    @Transactional
    public BookResponseDTO updateBook(BookRequestDTO bookRequestDTO, UUID id) {
        Book bookToUpdate = bookRepository.findBookById(id)
                .orElseThrow(() -> new BookNotFoundException(String.format("Book with id %s not found", id)));

        boolean isUpdated = false;

        if (bookRequestDTO.getTitle() != null && !bookRequestDTO.getTitle().equals(bookToUpdate.getTitle())) {
            bookToUpdate.setTitle(bookRequestDTO.getTitle());
            isUpdated = true;
        }

        if (bookRequestDTO.getAuthor() != null && !bookRequestDTO.getAuthor().equals(bookToUpdate.getAuthor())) {
            bookToUpdate.setAuthor(bookRequestDTO.getAuthor());
            isUpdated = true;
        }

        if (bookRequestDTO.getYear() != null && !bookRequestDTO.getYear().equals(bookToUpdate.getYear())) {
            bookToUpdate.setYear(bookRequestDTO.getYear());
            isUpdated = true;
        }

        if (isUpdated) {
            bookRepository.save(bookToUpdate);
            return new BookResponseDTO("Book updated successfully");
        } else {
            return new BookResponseDTO("No changes detected, book not updated");
        }
    }

    @Transactional
    public BookResponseDTO deleteBook(UUID id) {
        Book bookToDelete = bookRepository.findBookById(id)
                .orElseThrow(() -> new BookNotFoundException(String.format("Book with id %s not found", id)));

        bookRepository.delete(bookToDelete);
        return new BookResponseDTO("Book deleted successfully");
    }

    public BookRatingResponseDTO rateBook(UUID bookId, Double rating) {
        User currentUser = authService.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));

        Book bookToRate = bookRepository.findBookById(bookId)
                .orElseThrow(() -> new BookNotFoundException(String.format("Book with id %s not found", bookId)));

        if (rating < 0 || rating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 0 and 5");
        }

        Optional<BookRating> existingRating = bookRatingRepository.findBookRatingByUserIdAndBookId(currentUser.getId(), bookId);

        if (existingRating.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already rated this book");
        }

        BookRating bookRating = BookRating.builder()
                .book(bookToRate)
                .userId(currentUser.getId())
                .rating(rating)
                .build();
        bookRatingRepository.save(bookRating);

        List<BookRating> allRatings = bookRatingRepository.findBookRatingsByBookId(bookId);
        double averageRating = allRatings.stream()
                .mapToDouble(BookRating::getRating)
                .average()
                .orElse(0.0);

        Set<UUID> uniqueUSerIds = allRatings.stream()
                .map(BookRating::getUserId)
                .collect(Collectors.toSet());

        long ratingCount = uniqueUSerIds.size();

        bookToRate.setRating(averageRating);
        bookToRate.setRatingCount(ratingCount);
        bookRepository.save(bookToRate);
        return new BookRatingResponseDTO("Book rated successfully");
    }


    public User checkAuthorized() {
        return authService.getCurrentUser()
                .orElseThrow(() -> new NotAdminException("Not authorized"));
    }
}