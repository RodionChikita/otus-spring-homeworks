package ru.otus.hw.repositories;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.BookService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private Set<Long> genresIdsSet;

    @BeforeEach
    public void setUp() {
        author = authorRepository.findById(2).get();
        genresIdsSet = new HashSet<>();
        genresIdsSet.add(3L);
        genres = genreRepository.findAllByIds(genresIdsSet);
    }

    @Test
    public void testFindById() {
        Book book = new Book(0, "Test Book", author, genres);
        Book savedBook = bookRepository.save(book);

        Optional<Book> foundBook = bookService.findById(savedBook.getId());

        assertThat(foundBook)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(savedBook);
    }

    @Test
    public void testFindAll() {
        Book book1 = new Book(0, "Test Book 1", author, genres);
        Book book2 = new Book(0, "Test Book 2", author, genres);

        bookRepository.save(book1);
        bookRepository.save(book2);

        List<Book> allBooks = bookService.findAll();

        assertThat(allBooks)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(book1, book2);
    }

    @Test
    public void testInsert() {
        Book insertedBook = bookService.insert("New Book", author.getId(), genresIdsSet);

        Optional<Book> foundBook = bookService.findById(insertedBook.getId());

        assertThat(foundBook)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields()
                .isEqualTo(insertedBook);
    }

    @Test
    public void testUpdate() {
        Book book = new Book(0, "Test Book", author, genres);
        Book savedBook = bookRepository.save(book);

        Author newAuthor = authorRepository.findById(3).get();
        Set<Long> newGenresIdsSet = new HashSet<>();
        newGenresIdsSet.add(4L);
        List<Genre> newGenres = genreRepository.findAllByIds(newGenresIdsSet);

        Book updatedBook = bookService.update(savedBook.getId(), "Updated Book", newAuthor.getId(), newGenresIdsSet);

        assertThat(updatedBook)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new Book(savedBook.getId(), "Updated Book", newAuthor, newGenres));
    }

    @Test
    public void testDeleteById() {
        Book book = new Book(0, "Test Book", author, genres);
        Book savedBook = bookRepository.save(book);

        bookService.deleteById(savedBook.getId());

        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());

        assertThat(foundBook).isNotPresent();
    }

    @Test
    public void testInsertWithNonExistingAuthor() {
        assertThrows(EntityNotFoundException.class, () ->
                bookService.insert("New Book", 9999L, genresIdsSet));
    }

    @Test
    public void testInsertWithNonExistingGenre() {
        assertThrows(EntityNotFoundException.class, () ->
                bookService.insert("New Book", author.getId(), Set.of(9999L)));
    }

    @Test
    public void testUpdateWithNonExistingBook() {
        assertThrows(EntityNotFoundException.class, () ->
                bookService.update(9999L, "Updated Book", author.getId(), genresIdsSet));
    }
}