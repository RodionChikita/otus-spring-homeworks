package ru.otus.hw.services;

import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<Comment> findById(Long id);

    List<Comment> findAllByBookId(Long bookId);

    Comment insert(String text, Long bookId);

    Comment update(Long id, String text);

    void deleteById(Long id);
}
