package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.MapperToDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;
import static ru.otus.hw.mappers.MapperToDto.MAP_TO_BOOK_DTO_FUNCTION;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    @Override
    public BookDto findById(Long id) {
        return bookRepository.findById(id).map(MAP_TO_BOOK_DTO_FUNCTION)
                .orElseThrow(() -> new NotFoundException("Book with id %d not found".formatted(id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream().map(MAP_TO_BOOK_DTO_FUNCTION).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public BookDto insert(BookDto book) {
        if (isEmpty(book.getGenresIds())) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(book.getAuthorId())
                .orElseThrow(() -> new NotFoundException("Author with id %d not found".formatted(book.getAuthorId())));
        var genres = genreRepository.findByIdIn(book.getGenresIds());
        if (isEmpty(genres) || book.getGenresIds().size() != genres.size()) {
            throw new NotFoundException("One or all genres with ids %s not found".formatted(book.getGenresIds()));
        }

        var newBook = new Book(0L, book.getTitle(), author, genres);
        return MAP_TO_BOOK_DTO_FUNCTION.apply(bookRepository.save(newBook));
    }

    @Transactional
    @Override
    public BookDto update(BookDto book) {
        if (isEmpty(book.getGenresIds())) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(book.getAuthorId())
                .orElseThrow(() -> new NotFoundException("Author with id %d not found".formatted(book.getAuthorId())));
        var genres = genreRepository.findByIdIn(book.getGenresIds());
        if (isEmpty(genres) || book.getGenresIds().size() != genres.size()) {
            throw new NotFoundException("One or all genres with ids %s not found".formatted(book.getGenresIds()));
        }
        var updatedBook = bookRepository.findById(book.getId())
                .orElseThrow(() -> new NotFoundException("Book with id %d not found".formatted(book.getId())));
        updatedBook.setAuthor(author);
        updatedBook.setGenres(genres);
        updatedBook.setTitle(book.getTitle());
        return MapperToDto.MAP_TO_BOOK_DTO_FUNCTION.apply(bookRepository.save(updatedBook));
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
