package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    private final CommentConverter commentConverter;

    private final CommentService commentService;

    private final GenreService genreService;

    public String bookToString(Book book) {
        var commentString = commentService.findAllByBookId(book.getId()).stream()
                .map(commentConverter::commentToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        return "Id: %d, title: %s, author: {%s}, comments: {%s}".formatted(
                book.getId(),
                book.getTitle(),
                authorConverter.authorToString(book.getAuthor()), commentString);
    }
}
