package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {
    @PersistenceContext
    private final EntityManager em;

    private final BookRepository bookRepository;

    @Override
    public Optional<Comment> findById(long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.merge(comment);
            return comment;
        }
        return em.merge(comment);
    }

    @Override
    public void deleteById(Comment comment) {
        em.remove(comment);
    }

    @Override
    public Set<Comment> findAllByBookId(long bookId) {
        var book = bookRepository.findById(bookId);
        if (book.isEmpty()) {
            throw new EntityNotFoundException(("Book with id %d not found".formatted(bookId)));
        }
        return book.get().getComments();
    }
}
