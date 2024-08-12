package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    private final CommentConverter commentConverter;

    public String bookToString(Book book) {
        var genresString = book.getGenres().stream()
                .map(genreConverter::genreToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        String commentsString = null;
        if (book.getComments() != null) {
            commentsString = book.getComments().stream()
                    .map(commentConverter::commentToString)
                    .map("{%s}"::formatted)
                    .collect(Collectors.joining(", "));
        }
        return "Id: %d, title: %s, author: {%s}, comments: {%s}, genres: {%s}".formatted(
                book.getId(),
                book.getTitle(),
                authorConverter.authorToString(book.getAuthor()), commentsString, genresString);
    }
}
