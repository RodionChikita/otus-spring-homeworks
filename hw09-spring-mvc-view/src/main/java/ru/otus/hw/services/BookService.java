package ru.otus.hw.services;

import ru.otus.hw.dtos.BookDto;

import java.util.List;

public interface BookService {
    BookDto findById(Long id);

    List<BookDto> findAll();

    BookDto insert(BookDto book);

    BookDto update(BookDto book);

    void deleteById(Long id);
}
