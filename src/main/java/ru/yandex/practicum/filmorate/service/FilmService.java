package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.Collection;

/**
 * Бизнес‑слой для фильмов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

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

        if (!existing.getName().equals(newFilm.getName()) && newFilm.getName() != null) {
            existing.setName(newFilm.getName());
            log.info("Фильм {} изменил название на {}", newFilm.getId(), newFilm.getName());
        }

        if (newFilm.getDescription() != null
                && !existing.getDescription().equals(newFilm.getDescription())) {
            existing.setDescription(newFilm.getDescription());
            log.info("Фильм {} обновил описание", newFilm.getId());
        }

        if (!existing.getReleaseDate().equals(newFilm.getReleaseDate())
                && newFilm.getReleaseDate() != null) {
            existing.setReleaseDate(newFilm.getReleaseDate());
            log.info("Фильм {} обновил дату релиза", newFilm.getId());
        }

        if (!existing.getDuration().equals(newFilm.getDuration())) {
            existing.setDuration(newFilm.getDuration());
            log.info("Фильм {} обновил продолжительность", newFilm.getId());
        }

        return repo.save(existing);
    }
}

