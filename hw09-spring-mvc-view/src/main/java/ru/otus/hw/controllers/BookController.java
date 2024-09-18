package ru.otus.hw.controllers;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BookController {
    private static final Function<Author, AuthorDto> MAP_AUTHOR_TO_DTO_FUNCTION =
            a -> new AuthorDto(a.getId(), a.getFullName());

    private static final Function<Genre, GenreDto> MAP_GENRES_TO_DTO_FUNCTION =
            a -> new GenreDto(a.getId(), a.getName());

    private static final Function<Book, BookDto> MAP_TO_BOOK_DTO_FUNCTION =
            b -> new BookDto(b.getId(), b.getTitle(), new AuthorDto(b.getAuthor().getId(), b.getAuthor().getFullName()),
                    b.getGenres().stream().map(MAP_GENRES_TO_DTO_FUNCTION).collect(Collectors.toList()));

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/")
    public String listPage(Model model) {
        List<BookDto> books = bookService.findAll().stream()
                .map(MAP_TO_BOOK_DTO_FUNCTION).collect(Collectors.toList());
        model.addAttribute("books", books);
        return "list";
    }

    @GetMapping("/edit")
    public String editPage(@RequestParam("id") long id, Model model) {
        BookDto book = bookService.findById(id).map(MAP_TO_BOOK_DTO_FUNCTION).get();
        List<AuthorDto> authors = authorService.findAll().stream()
                .map(MAP_AUTHOR_TO_DTO_FUNCTION).collect(Collectors.toList());
        List<GenreDto> genres = genreService.findAll().stream()
                .map(MAP_GENRES_TO_DTO_FUNCTION).collect(Collectors.toList());
        model.addAttribute("book",book);
        model.addAttribute("authors", authors);
        model.addAttribute("genres", genres);
        return "edit";
    }

    @GetMapping("/book")
    public String insertBookInPage(Model model) {
        List<AuthorDto> authors = authorService.findAll().stream()
                .map(MAP_AUTHOR_TO_DTO_FUNCTION).collect(Collectors.toList());
        List<GenreDto> genres = genreService.findAll().stream()
                .map(MAP_GENRES_TO_DTO_FUNCTION).collect(Collectors.toList());
        model.addAttribute("book", new BookDto());
        model.addAttribute("authors", authors);
        model.addAttribute("genres", genres);
        return "insert";
    }

    @PostMapping("/edit")
    public String editBook(BookDto book) {
        bookService.update(book.getId(), book.getTitle(), book.getAuthor().getId(),
                book.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet()));
        return "redirect:/";
    }

    @PostMapping("/book")
    public String insertBook(BookDto book) {
        bookService.insert(book.getTitle(), book.getAuthor().getId(),
                book.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet()));
        return "redirect:/";
    }

    @GetMapping("/delete")
    public String deleteBook(@RequestParam("id") long id, Model model) {
        bookService.deleteById(id);
        List<BookDto> books = bookService.findAll().stream().map(MAP_TO_BOOK_DTO_FUNCTION).collect(Collectors.toList());
        model.addAttribute("books", books);
        return "list";
    }
}