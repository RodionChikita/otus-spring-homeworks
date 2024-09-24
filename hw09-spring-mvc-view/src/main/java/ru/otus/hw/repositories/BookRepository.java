package ru.otus.hw.repositories;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Nonnull
    @EntityGraph("book-authors-genres-entity-graph")
    Optional<Book> findById(long id);

    @Nonnull
    @EntityGraph("book-authors-entity-graph")
    List<Book> findAll();
}
