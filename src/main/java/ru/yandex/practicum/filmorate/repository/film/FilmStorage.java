package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Optional<Film> findById(Long id);

    Film add(Film film);

    Film update(Film film);

    void delete(Long id);

    void addLike(Long filmId, Long userId) throws DuplicateException;

    void removeLike(Long filmId, Long userId);

    List<Film> getPopular(int count);
}

