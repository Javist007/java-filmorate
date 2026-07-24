package ru.yandex.practicum.filmorate.repository.film.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.BaseStorage;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@Primary
public class FilmDBRepository extends BaseStorage<Film> implements FilmStorage {

    public FilmDBRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> findAll() {
        log.debug("Возвращаем список всех фильмов");
        return findMany(FilmSQLRequests.FIND_ALL_FILMS);
    }

    @Override
    public Optional<Film> findById(Long id) {
        log.debug("Возвращаем фильм по ID: {}", id);
        return findOne(FilmSQLRequests.FIND_FILM_BY_ID, id);
    }

    @Override
    public Film create(Film film) {
        long id = insert(FilmSQLRequests.INSERT_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        log.debug("Добавлен фильм ID: {}", id);
        return film;
    }

    @Override
    public Film update(Film film) {
        update(FilmSQLRequests.UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        log.debug("Обновлен фильм ID: {}", film.getId());
        return film;
    }

    @Override
    public void delete(Long id) {
        delete(FilmSQLRequests.DELETE_FILM, id);
        log.debug("Удален фильм ID: {}", id);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        log.debug("Возвращаем топ {} популярных фильмов", count);
        return findMany(FilmSQLRequests.FIND_POPULAR_FILMS, count);
    }
}
