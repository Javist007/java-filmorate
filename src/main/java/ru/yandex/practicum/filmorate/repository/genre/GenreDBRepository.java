package ru.yandex.practicum.filmorate.repository.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.BaseStorage;

import java.util.*;

@Repository
@Slf4j
public class GenreDBRepository extends BaseStorage<Genre> implements GenreStorage {

    NamedParameterJdbcTemplate namedJdbc;

    public GenreDBRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
        this.namedJdbc = new NamedParameterJdbcTemplate(jdbc);
    }

    @Override
    public List<Genre> findAll() {
        log.debug("Возвращаем список всех жанров");
        return findMany(GenreSQLRequests.FIND_ALL_GENRES);
    }

    @Override
    public Optional<Genre> findById(long id) {
        log.debug("Возвращаем жанр по ID: {}", id);
        return findOne(GenreSQLRequests.FIND_GENRE_BY_ID, id);
    }

    @Override
    public Map<Long, List<Genre>> findGenresByFilmIds(Set<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, List<Genre>> result = new HashMap<>();
        namedJdbc.query(GenreSQLRequests.FIND_BY_FILM_IDS, Map.of("ids", filmIds), resultSet -> {
            long filmId = resultSet.getLong("film_id");
            Genre genre = new Genre();
            genre.setId(resultSet.getLong("id"));
            genre.setName(resultSet.getString("name"));
            result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
        });
        return result;
    }

    @Override
    public void addGenresToFilm(long filmId, List<Long> genreIds) {
        log.debug("Добавляем жанр к фильму ID: {}", filmId);
        jdbc.batchUpdate(GenreSQLRequests.INSERT_FILM_GENRE, genreIds, genreIds.size(),
                (ps, genreId) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, genreId);
                });
    }

    @Override
    public void deleteGenresFromFilm(long filmId) {
        log.debug("Удаляем жанр из фильма ID: {}", filmId);
        delete(GenreSQLRequests.DELETE_FILM_GENRES, filmId);
    }

    @Override
    public Set<Long> findExistGenreId(Set<Long> ids) {
        log.debug("Поиск жанров по списку ID");
        if (ids.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(namedJdbc.queryForList(GenreSQLRequests.FIND_EXISTING_ID,
                Map.of("ids", ids), Long.class));
    }
}
