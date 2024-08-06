package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    private final RowMapper<Book> bookRowMapper = new RowMapper<Book>() {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book();
            book.setId(rs.getLong("id"));
            book.setTitle(rs.getString("title"));

            Author author = new Author();
            author.setId(rs.getLong("author_id"));
            author.setFullName(rs.getString("full_name"));
            book.setAuthor(author);

            return book;
        }
    };

    private final RowMapper<Genre> genreRowMapper = new RowMapper<Genre>() {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            Genre genre = new Genre();
            genre.setId(rs.getLong("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }
    };

    @Override
    public Optional<Book> findById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        String bookQuery = "SELECT b.id, b.title, a.id as author_id, a.full_name " +
                "FROM books b " +
                "JOIN authors a ON b.author_id = a.id " +
                "WHERE b.id = :id";

        Book book = namedParameterJdbcOperations.queryForObject(bookQuery, params, bookRowMapper);

        if (book != null) {
            String genreQuery = "SELECT g.id, g.name "
                    + "FROM genres g " +
                    "JOIN books_genres bg ON g.id = bg.genre_id " +
                    "WHERE bg.book_id = :id";
            List<Genre> genres = namedParameterJdbcOperations.query(genreQuery, params, genreRowMapper);
            book.setGenres(genres);
        }

        return Optional.of(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        removeGenresRelationsFor(findById(id).get());
        namedParameterJdbcOperations.update("DELETE from BOOKS where id = :id", params);
    }

    private List<Book> getAllBooksWithoutGenres() {
        return namedParameterJdbcOperations.query("SELECT id, title, author_id from BOOKS",
                new BookWithoutGenreRowMapper(authorRepository));
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return namedParameterJdbcOperations.query("SELECT book_id, genre_id from BOOKS_GENRES",
                new BookGenreRelationRowMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres, List<BookGenreRelation> relations) {
        for (Book book : booksWithoutGenres) {
            Set<Long> genresIds = new HashSet<>();
            for (BookGenreRelation relation : relations) {
                if (book.getId() == relation.bookId) {
                    genresIds.add(relation.genreId);
                }
            }
            book.setGenres(genreRepository.findAllByIds(genresIds));
        }
        // Добавить книгам (booksWithoutGenres) жанры (genres) в соответствии со связями (relations)
    }

    public Book insert(Book book) {
        String insertBookSql = "INSERT INTO books (title, author_id) VALUES (:title, :authorId)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        SqlParameterSource parameters = new MapSqlParameterSource().addValue
                ("title", book.getTitle()).addValue("authorId", book.getAuthor().getId());

        namedParameterJdbcOperations.update(insertBookSql, parameters, keyHolder, new String[]{"id"});

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    public Book update(Book book) {
        String updateBookSql = "UPDATE books SET title = :title, author_id = :authorId WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource().addValue
                ("title", book.getTitle()).addValue("authorId", book.getAuthor().getId()).addValue("id", book.getId());

        int rowsAffected = namedParameterJdbcOperations.update(updateBookSql, parameters);

        if (rowsAffected == 0) {
            throw new EntityNotFoundException("No book found with id " + book.getId());
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        String insertGenreRelationSql = "INSERT INTO books_genres (book_id, genre_id) VALUES (:bookId, :genreId)";

        List<MapSqlParameterSource> batchArgs = book.getGenres().stream().map(genre ->
                new MapSqlParameterSource().addValue("bookId", book.getId()).addValue
                        ("genreId", genre.getId())).toList();

        namedParameterJdbcOperations.batchUpdate(insertGenreRelationSql, batchArgs.toArray(new SqlParameterSource[0]));
    }

    private void removeGenresRelationsFor(Book book) {
        Map<String, Object> params = Collections.singletonMap("id", book.getId());
        namedParameterJdbcOperations.update("DELETE from BOOKS_GENRES where book_id = :id", params);
    }

    private static class BookWithoutGenreRowMapper implements RowMapper<Book> {
        private final AuthorRepository authorRepository;

        @Autowired
        private BookWithoutGenreRowMapper(AuthorRepository authorRepository) {
            this.authorRepository = authorRepository;
        }

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong("id");
            String title = rs.getString("title");
            Optional<Author> authorOptional = authorRepository.findById(rs.getLong("author_id"));
            if (authorOptional.isEmpty()) {
                throw new EntityNotFoundException("Author not found");
            }
            return new Book(id, title, authorOptional.get(), null);
        }
    }

    private static class BookGenreRelationRowMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int i) throws SQLException {
            long bookId = rs.getLong("book_id");
            long genreId = rs.getLong("genre_id");
            return new BookGenreRelation(bookId, genreId);
        }
    }

    // Использовать для findById
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            return null;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

}
