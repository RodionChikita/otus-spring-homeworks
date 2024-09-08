package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        try {
        EntityGraph<?> entityGraph = em.getEntityGraph("book-authors-genres-comments-entity-graph");
            Book book = em.createQuery("SELECT b FROM Book b WHERE b.id = :id", Book.class)
                    .setParameter("id", id)
                    .setHint(FETCH.getKey(), entityGraph)
                    .getSingleResult();
            return Optional.of(book);
        } catch (Exception exception) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(id));
        }
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<?> entityGraph = em.getEntityGraph("book-authors-genres-comments-entity-graph");
        TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b", Book.class);
        query.setHint(FETCH.getKey(), entityGraph);
        return query.getResultList();
    }


    @Override
    public Book save(Book book) {
        return em.merge(book);
    }

    @Override
    public void deleteById(Book book) {
        em.remove(book);
    }
}