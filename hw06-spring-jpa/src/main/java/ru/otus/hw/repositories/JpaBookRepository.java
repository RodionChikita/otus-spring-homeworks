package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import java.util.List;
import java.util.Optional;
import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Transactional
@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        Book book = em.find(Book.class, id);
        System.out.println(book.getGenres().get(1));
        System.out.println(book.getComments().get(1));
        return Optional.of(book);
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<?> entityGraph = em.getEntityGraph("book-authors-entity-graph");
        TypedQuery<Book> query = em.createQuery("select distinct b from Book b " +
                "left join fetch b.comments", Book.class);
        query.setHint(FETCH.getKey(), entityGraph);
        List<Book> books = query.getResultList();
        System.out.println(books.get(1).getGenres());
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.merge(book);
            return book;
        }
        return em.merge(book);
    }

    @Override
    public void deleteById(long id) {
        Query query = em.createQuery("delete " +
                "from Book b " +
                "where b.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }
}