package ru.otus.hw.repositories;

import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentRepository {
    Optional<Comment> findById(long id);

    Comment save(Comment comment);

    void deleteById(long id);

    List<Comment> findAllByBookId(long bookId);
}
