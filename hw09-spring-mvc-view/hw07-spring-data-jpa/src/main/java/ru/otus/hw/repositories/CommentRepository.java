package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findById(long id);

    @Modifying
    Comment save(Comment comment);

    @Modifying
    void deleteById(long id);

    @Query(value = """
            SELECT id, text, book_id FROM comments
            WHERE comments.book_id = :bookId
            """,
            nativeQuery = true)
    List<Comment> findAllByBookId(long bookId);
}
