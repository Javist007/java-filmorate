package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;
import ru.yandex.practicum.filmorate.service.util.Updater;

import java.util.Collection;
import java.util.List;

/**
 * Бизнес‑слой для фильмов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    public static final String FILM = "Фильм";

    private final FilmStorage filmRepository;
    private final UserStorage userRepository;

    public Collection<Film> findAll() {
        log.info("Получаем список всех фильмов");
        return filmRepository.findAll();
    }

    public Film create(Film film) {
        Film saved = filmRepository.add(film);
        log.info("Добавлен фильм '{}' с ID {}", film.getName(), saved.getId());
        return saved;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new NotFoundException("ID должен быть указан");
        }
        Film existing = getById(newFilm.getId());

        Updater.updateField(log, existing.getId(), FILM, "name", newFilm.getName(),
                existing.getName(), existing::setName);
        Updater.updateField(log, existing.getId(), FILM, "description", newFilm.getDescription(),
                existing.getDescription(), existing::setDescription);
        Updater.updateField(log, existing.getId(), FILM, "releaseDate", newFilm.getReleaseDate(),
                existing.getReleaseDate(), existing::setReleaseDate);
        Updater.updateField(log, existing.getId(), FILM, "duration", newFilm.getDuration(),
                existing.getDuration(), existing::setDuration);

        return filmRepository.add(existing);
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getById(filmId);
        isUserNotFound(userId);
        if (film.getLikedBy().contains(userId)) {
            log.info("Пользователю ID: {} уже понравился этот фильм", userId);
            throw new DuplicateException("Пользователю уже понравился этот фильм");
        }
        filmRepository.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        isUserNotFound(userId);
        Film film = getById(filmId);
        filmRepository.removeLike(film.getId(), userId);
    }

    public List<Film> getPopular(int count) {
        log.info("Получаем {} самых популярных фильмов", count);
        return filmRepository.getPopular(count);
    }

    public Film getById(Long id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Фильм не найден. ID: {}", id);
                    return new NotFoundException(
                            "Фильм ID: = " + id + " не найден");
                });
    }

    private void isUserNotFound(Long userId) {
        if (!userRepository.exists(userId)) {
            log.warn("Пользователь с ID {} не существует", userId);
            throw new NotFoundException("Пользователь c ID " + userId + " не существует!");
        }
    }
}
