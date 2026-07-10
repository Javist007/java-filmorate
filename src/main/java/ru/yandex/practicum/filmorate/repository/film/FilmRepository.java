package ru.yandex.practicum.filmorate.repository.film;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.like.LikeStorage;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Репозиторий для фильмов.
 */
@Repository
@Getter
@Slf4j
public class FilmRepository implements FilmStorage {

    private final Map<Long, Film> filmStorage = new LinkedHashMap<>();
    private final LikeStorage likeStorage;
    private final AtomicLong idGenerator = new AtomicLong(0);

    public FilmRepository(LikeStorage likeStorage) {
        this.likeStorage = likeStorage;
    }

    @Override
    public Collection<Film> findAll() {
        log.debug("Возвращаем {} фильмов из репозитория", filmStorage.size());
        return List.copyOf(filmStorage.values());
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(filmStorage.get(id));
    }

    @Override
    public Film create(Film film) {
        if (film.getId() == null) {
            film.setId(idGenerator.incrementAndGet());
        }
        filmStorage.put(film.getId(), film);
        log.debug("Фильм '{}' сохранён с ID {}", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        filmStorage.put(film.getId(), film);
        log.debug("Фильм с ID {} обновлён", film.getId());
        return film;
    }

    @Override
    public void delete(Long id) {
        filmStorage.remove(id);
        log.info("Удалён фильм с ID {}", id);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        return filmStorage.values().stream()
                .sorted((f1, f2) -> {
                    int likes1 = likeStorage.getUserIds(f1.getId()).size();
                    int likes2 = likeStorage.getUserIds(f2.getId()).size();
                    return Integer.compare(likes2, likes1);
                })
                .limit(count)
                .toList();
    }
}
