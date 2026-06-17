package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.service.util.Updater;

import java.util.Collection;

/**
 * Бизнес‑слой для фильмов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    public static final String FILM = "Фильм";
    private final FilmRepository repo;

    public Collection<Film> findAll() {
        log.info("Получаем список всех фильмов");
        return repo.findAll();
    }

    public Film create(Film film) {
        Film saved = repo.save(film);
        log.info("Добавлен фильм '{}' с ID {}", film.getName(), saved.getId());
        return saved;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new NotFoundException("ID должен быть указан");
        }
        Film existing = repo.findById(newFilm.getId())
                .orElseThrow(() -> {
                    log.error("Фильм не найден. ID: {}", newFilm.getId());
                    return new NotFoundException(
                            "Фильм ID: = " + newFilm.getId() + " не найден");
                });

        Updater.updateField(log,
                existing.getId(), FILM, "name", newFilm.getName(), existing.getName(),
                existing::setName);

        Updater.updateField(log, existing.getId(), FILM, "description", newFilm.getDescription(),
                existing.getDescription(),
                existing::setDescription);

        Updater.updateField(log, existing.getId(), FILM, "releaseDate", newFilm.getReleaseDate(),
                existing.getReleaseDate(),
                existing::setReleaseDate);

        Updater.updateField(log, existing.getId(), FILM, "duration", newFilm.getDuration(),
                existing.getDuration(),
                existing::setDuration);

        return repo.save(existing);
    }
}

