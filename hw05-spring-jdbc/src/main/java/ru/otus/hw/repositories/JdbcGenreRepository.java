package ru.otus.hw.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Repository
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Autowired
    public JdbcGenreRepository(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public List<Genre> findAll() {
        return namedParameterJdbcOperations.query("SELECT id, name from GENRES", new GnreRowMapper());
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);

        List<Genre> genres = namedParameterJdbcOperations.query(
                "SELECT id, name from GENRES WHERE id IN (:ids)",
                parameters, new GnreRowMapper());
        return genres;
    }

    private static class GnreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            return new Genre(id, name);
        }
    }
}
