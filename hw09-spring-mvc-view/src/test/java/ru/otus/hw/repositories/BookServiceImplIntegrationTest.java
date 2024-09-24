package ru.otus.hw.repositories;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.BookService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional(propagation = Propagation.NEVER)
public class BookServiceImplIntegrationTest {
    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private BookRepository bookRepository;
    private Author author;
    private List<Genre> genres;
    private List<Long> genresIdsSet;
    private BookDto bookDto;
    @BeforeEach
    public void setUp() {
        author = authorRepository.findById(2L).get();
        genresIdsSet = new ArrayList<>();
        genresIdsSet.add(3L);
        genres = genreRepository.findByIdIn(genresIdsSet);
        bookDto = new BookDto(0L,"New Book", author.getId(), genresIdsSet);
    }

    @Test
    public void testFindById() {
        BookDto savedBook = bookService.insert(bookDto);

        BookDto foundBook = bookService.findById(savedBook.getId());

        assertThat(foundBook)
                .usingRecursiveComparison()
                .isEqualTo(savedBook);
    }

    @Test
    public void testFindAll() {

        BookDto book1 = bookService.insert(new BookDto(0L,"Test Book 1", author.getId(), genresIdsSet));
        BookDto book2 = bookService.insert(new BookDto(0L,"Test Book 2", author.getId(), genresIdsSet));


        List<BookDto> allBooks = bookService.findAll();

        assertThat(allBooks)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(book1, book2);
    }
    @Test
    public void testInsert() {
        BookDto insertedBook = bookService.insert(bookDto);

        BookDto foundBook = bookService.findById(insertedBook.getId());

        assertThat(foundBook)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookDto);
    }

    @Test
    public void testUpdate() {
        BookDto savedBook = bookService.insert(bookDto);

        Author newAuthor = authorRepository.findById(3L).get();
        List<Long> newGenresIdsSet = new ArrayList<>();
        newGenresIdsSet.add(4L);
        List<Genre> newGenres = genreRepository.findByIdIn(newGenresIdsSet);
        BookDto updatedBook = bookService.update(new BookDto(savedBook.getId(), "Updated Book", newAuthor.getId(), newGenresIdsSet));

        assertThat(updatedBook)
                .usingRecursiveComparison()
                .isEqualTo(new BookDto(savedBook.getId(), "Updated Book", newAuthor.getId(), newGenres.stream().map(Genre::getId).collect(Collectors.toList())));
    }

    @Test
    public void testDeleteById() {
        BookDto savedBook = bookService.insert(bookDto);

        bookService.deleteById(savedBook.getId());

        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());
        assertThat(foundBook).isNotPresent();
    }
    @Test
    public void testInsertWithNonExistingAuthor() {
        assertThrows(NotFoundException.class, () ->
                bookService.insert(new BookDto(0L,"New Book", 9999L, genresIdsSet)));
    }
    @Test
    public void testInsertWithNonExistingGenre() {
        assertThrows(NotFoundException.class, () ->
                bookService.insert(new BookDto(0L,"New Book", author.getId(), List.of(9999L))));
    }
    @Test
    public void testUpdateWithNonExistingBook() {
        assertThrows(NotFoundException.class, () ->
                bookService.update(new BookDto(9999L, "Updated Book", author.getId(), genresIdsSet)));
    }
}