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
import java.util.Set;

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
    public List<Film> getPopular(Integer count, Long genreId, Integer year) {
        log.debug("Возвращаем топ {} популярных фильмов с фильтрами: genreId={}, year={}", count, genreId, year);
        return findMany(FilmSQLRequests.FIND_POPULAR_FILMS, genreId, genreId, year, year, count);
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        log.debug("Выборка общих фильмов из БД для пользователей: {} и {}", userId, friendId);
        return findMany(FilmSQLRequests.FIND_COMMON_FILMS, userId, friendId);
    }

    @Override
    public List<Film> findDirectorFilms(long directorId, String sortType) {
        log.debug("Получение фильмов по режиссеру ID:{}, отсортированных по - {}", directorId, sortType);
        return findMany(sortType.equalsIgnoreCase("likes")
                ? FilmSQLRequests.FIND_FILMS_LIKES_SORT
                : FilmSQLRequests.FIND_FILMS_YEAR_SORT, directorId);
    }

    @Override
    public List<Film> search(String query, Set<String> by) {
        log.debug("Поиск фильмов в базе, запрос: '{}', фильтры: '{}'", query, by);
        String searchQuery = query.toLowerCase().trim();

        boolean title = by.contains("title");
        boolean director = by.contains("director");

        if (title && director) {
            return findMany(FilmSQLRequests.SEARCH_BOTH, searchQuery, searchQuery);
        } else if (director) {
            return findMany(FilmSQLRequests.SEARCH_DIRECTOR, searchQuery);
        } else {
            return findMany(FilmSQLRequests.SEARCH_TITLE, searchQuery);
        }
    }
}
