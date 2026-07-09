package ru.yandex.practicum.filmorate.repository.film;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Репозиторий для фильмов.
 */
@Repository
@Getter
@Slf4j
public class FilmRepository implements FilmStorage {

    private final Map<Long, Film> storage = new LinkedHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public Collection<Film> findAll() {
        log.debug("Возвращаем {} фильмов из репозитория", storage.size());
        return List.copyOf(storage.values());
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Film add(Film film) {
        if (film.getId() == null) {
            film.setId(idGenerator.incrementAndGet());
        }
        storage.put(film.getId(), film);
        log.debug("Фильм '{}' сохранён с ID {}", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        storage.put(film.getId(), film);
        log.debug("Фильм с ID {} обновлён", film.getId());
        return film;
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
        log.info("Удалён фильм с ID {}", id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        storage.get(filmId).getLikedBy().add(userId);
        log.debug("Пользователь ID {} добавил лайк фильму ID {}", userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        storage.get(filmId).getLikedBy().remove(userId);
        log.debug("Пользователь ID {} удалил лайк фильма ID {}", userId, filmId);
    }

    @Override
    public List<Film> getPopular(int count) {
        return storage.values().stream()
                .sorted((f1, f2) -> Integer.compare(
                        f2.getLikedBy() == null ? 0 : f2.getLikedBy().size(),
                        f1.getLikedBy() == null ? 0 : f1.getLikedBy().size()))
                .limit(count)
                .toList();
    }
}
