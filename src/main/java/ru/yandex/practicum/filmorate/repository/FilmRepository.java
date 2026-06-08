package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Репозиторий для фильмов.
 */
@Repository
public class FilmRepository {

    private final Map<Long, Film> storage = new LinkedHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public Collection<Film> findAll() {
        return List.copyOf(storage.values());
    }

    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Film save(Film film) {
        if (film.getId() == null) {
            film.setId(idGenerator.incrementAndGet());
        }
        storage.put(film.getId(), film);
        return film;
    }

    public void delete(Long id) {
        storage.remove(id);
    }
}

