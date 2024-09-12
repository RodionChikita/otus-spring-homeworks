package ru.otus.hw.controllers;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;

    @GetMapping("/")
    public String listPage(Model model) {
        List<Book> books = bookService.findAll();
        model.addAttribute("books", books);
        return "list";
    }

    @GetMapping("/edit")
    public String editPage(@RequestParam("id") long id, Model model) {
        Book book = bookService.findById(id).orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));
        List<Author> authors = authorService.findAll();
        List<Genre> genres = genreService.findAll();
        model.addAttribute("book",book);
        model.addAttribute("authors", authors);
        model.addAttribute("genres", genres);
        return "edit";
    }

    @GetMapping("/insert")
    public String insertBookInPage(Model model) {
        List<Author> authors = authorService.findAll();
        List<Genre> genres = genreService.findAll();
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authors);
        model.addAttribute("genres", genres);
        return "insert";
    }

    @PostMapping("/edit")
    public String editBook(Book book) {
        bookService.update(book.getId(), book.getTitle(), book.getAuthor().getId(), book.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));
        return "redirect:/";
    }

    @PostMapping("/insert")
    public String insertBook(Book book) {
        bookService.insert(book.getTitle(), book.getAuthor().getId(), book.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));
        return "redirect:/";
    }

    @GetMapping("/delete")
    public String deleteBook(@RequestParam("id") long id, Model model) {
        bookService.deleteById(id);
        List<Book> books = bookService.findAll();
        model.addAttribute("books", books);
        return "list";
    }
}
