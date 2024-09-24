package ru.otus.hw.services;

import ru.otus.hw.dtos.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto findById(Long id);

    List<CommentDto> findAllByBookId(Long bookId);

    CommentDto insert(String text, Long bookId);

    CommentDto update(Long id, String text, Long bookId);

    void deleteById(Long id);
}
