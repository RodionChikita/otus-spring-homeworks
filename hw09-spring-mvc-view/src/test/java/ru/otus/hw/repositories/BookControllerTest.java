package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.BookController;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    private Author author;
    private Genre genre;
    private Book book;
    private BookDto bookDto;
    private AuthorDto authorDto;
    private GenreDto genreDto;

    @BeforeEach
    public void setUp() {
        author = new Author(1L, "Test Author");
        genre = new Genre(1L, "Test Genre");
        book = new Book(1L, "Test Book", author, List.of(genre));
        authorDto = new AuthorDto(1L, "Test Author");
        genreDto = new GenreDto(1L, "Test Genre");
        bookDto = new BookDto(1L, "Test Book", authorDto, List.of(genreDto));
    }

    @Test
    public void testListPage() throws Exception {
        Mockito.when(bookService.findAll()).thenReturn(List.of(book));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("list"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", List.of(bookDto)));
    }

    @Test
    public void testEditPage() throws Exception {
        Mockito.when(bookService.findById(anyLong())).thenReturn(Optional.of(book));
        Mockito.when(authorService.findAll()).thenReturn(List.of(author));
        Mockito.when(genreService.findAll()).thenReturn(List.of(genre));

        mockMvc.perform(get("/edit")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attributeExists("book", "authors", "genres"))
                .andExpect(model().attribute("book", bookDto))
                .andExpect(model().attribute("authors", List.of(authorDto)))
                .andExpect(model().attribute("genres", List.of(genreDto)));
    }

    @Test
    public void testInsertBookInPage() throws Exception {
        Mockito.when(authorService.findAll()).thenReturn(List.of(author));
        Mockito.when(genreService.findAll()).thenReturn(List.of(genre));

        mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(view().name("insert"))
                .andExpect(model().attributeExists("book", "authors", "genres"))
                .andExpect(model().attribute("authors", List.of(authorDto)))
                .andExpect(model().attribute("genres", List.of(genreDto)));
    }

    @Test
    public void testEditBook() throws Exception {
        Book updatedBook = new Book(1L, "Updated Book", author, List.of(genre));
        Mockito.when(bookService.update(anyLong(), any(), anyLong(), any()))
                .thenReturn(updatedBook);

        mockMvc.perform(post("/edit")
                        .param("id", "1")
                        .param("title", "Updated Book")
                        .param("author.id", "1")
                        .param("genres[0].id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Mockito.verify(bookService).update(eq(1L), eq("Updated Book"), eq(1L), any());
    }

    @Test
    public void testInsertBook() throws Exception {
        Book newBook = new Book(0L, "New Book", author, List.of(genre));
        Mockito.when(bookService.insert(anyString(), anyLong(), anySet())).thenReturn(newBook);
        mockMvc.perform(post("/book")
                        .param("title", "New Book")
                        .param("author.id", "1")
                        .param("genres[0].id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        Mockito.verify(bookService).insert(eq("New Book"), eq(1L), anySet());
    }

    @Test
    public void testDeleteBook() throws Exception {
        Mockito.doNothing().when(bookService).deleteById(anyLong());
        Mockito.when(bookService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/delete")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("list"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", List.of()));

        Mockito.verify(bookService).deleteById(eq(1L));
    }
}