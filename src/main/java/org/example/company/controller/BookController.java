package org.example.company.controller;

import lombok.RequiredArgsConstructor;
import org.example.company.dto.book.BookRequestDTO;
import org.example.company.dto.book.BookResponseDTO;
import org.example.company.exception.BookNotFoundException;
import org.example.company.exception.NotAdminException;
import org.example.company.model.Book;
import org.example.company.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BookController {

    private final BookService bookService;

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/books/add")
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookRequestDTO bookRequestDTO) {
        try {
            BookResponseDTO response = bookService.createBook(bookRequestDTO);
            return ResponseEntity.ok(response);
        } catch (NotAdminException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BookResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BookResponseDTO(e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/books/update/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@RequestBody BookRequestDTO bookRequestDTO, @PathVariable UUID id) {
        try {
            BookResponseDTO response = bookService.updateBook(bookRequestDTO, id);
            return ResponseEntity.ok(response);
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BookResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BookResponseDTO(e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/books/delete/{id}")
    public ResponseEntity<BookResponseDTO> deleteBook(@PathVariable UUID id) {
        try {
            BookResponseDTO response = bookService.deleteBook(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BookResponseDTO(e.getMessage()));
        }
    }
}
