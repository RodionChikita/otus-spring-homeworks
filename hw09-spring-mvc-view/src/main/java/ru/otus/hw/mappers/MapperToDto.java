package ru.otus.hw.mappers;

import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.dtos.CommentDto;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.function.Function;
import java.util.stream.Collectors;

public class MapperToDto {
    public static final Function<Author, AuthorDto> MAP_TO_AUTHOR_DTO_FUNCTION =
            a -> new AuthorDto(a.getId(), a.getFullName());

    public static final Function<Genre, GenreDto> MAP_TO_GENRES_DTO_FUNCTION =
            a -> new GenreDto(a.getId(), a.getName());

    public static final Function<Book, BookDto> MAP_TO_BOOK_DTO_FUNCTION =
            b -> new BookDto(b.getId(), b.getTitle(), b.getAuthor().getId(),
                    b.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));

    public static final Function<Comment, CommentDto> MAP_TO_COMMENT_DTO_FUNCTION =
            c -> new CommentDto(c.getId(), c.getText(), MAP_TO_BOOK_DTO_FUNCTION.apply(c.getBook()));
}
