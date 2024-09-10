package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {
    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Comment> findById(long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public Comment save(Comment comment) {
        return em.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        em.remove(findById(id));
    }

    @Override
    public List<Comment> findAllByBookId(long bookId) {
        return em.createQuery("select c from Comment c where c.book_id = :bookId", Comment.class).getResultList();
    }
}
