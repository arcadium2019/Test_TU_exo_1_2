package org.example.exo14.repository;

import org.example.exo14.model.Book;

import java.util.Optional;

public interface BookRepository {
    Book save(Book book);
    Optional<Book> findById(String id);
}
