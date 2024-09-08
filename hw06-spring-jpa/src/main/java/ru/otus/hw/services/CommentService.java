package ru.otus.hw.services;

import ru.otus.hw.models.Comment;

import java.util.Optional;
import java.util.Set;

public interface CommentService {
    Optional<Comment> findById(long id);

    Set<Comment> findAllByBookId(long bookId);

    Comment insert(String text, long bookId);

    Comment update(long id, String text, long bookId);

    void deleteById(long id);
}
