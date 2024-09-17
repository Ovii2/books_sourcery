package org.example.company.repository;

import org.example.company.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

    boolean existsByTitle(String title);

    Optional<Book> findBookById(UUID id);
}
