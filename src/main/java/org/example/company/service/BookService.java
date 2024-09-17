package org.example.company.service;

import lombok.RequiredArgsConstructor;
import org.example.company.dto.book.BookRequestDTO;
import org.example.company.dto.book.BookResponseDTO;
import org.example.company.exception.BookNotFoundException;
import org.example.company.exception.NotAdminException;
import org.example.company.model.Book;
import org.example.company.model.User;
import org.example.company.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthService authService;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
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

    public User checkAuthorized() {
        return authService.getCurrentUser()
                .orElseThrow(() -> new NotAdminException("Not authorized"));
    }
}