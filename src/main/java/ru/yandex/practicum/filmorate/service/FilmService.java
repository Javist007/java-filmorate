package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmRequest;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.like.LikeStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;
import ru.yandex.practicum.filmorate.service.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.service.util.Updater;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    private final LikeStorage likeStorage;

    public Collection<FilmResponse> findAll() {
        log.info("Получаем список всех фильмов");
        return filmRepository.findAll().stream()
                .map(FilmMapper::toDto)
                .collect(Collectors.toList());
    }

    public FilmResponse getByIdResponse(Long id) {
        Film film = getById(id);
        return FilmMapper.toDto(film);
    }

    public FilmResponse create(FilmRequest request) {
        Film saved = FilmMapper.toEntity(request);
        filmRepository.create(saved);

        log.info("Добавлен фильм '{}' с ID {}", request.getName(), saved.getId());
        return FilmMapper.toDto(saved);
    }

    public FilmResponse update(FilmRequest request) {
        if (request.getId() == null) {
            throw new NotFoundException("ID должен быть указан");
        }
        getById(request.getId());

        Film existing = FilmMapper.toEntity(request);

        Updater.updateField(log, existing.getId(), FILM, "name", request.getName(),
                existing.getName(), existing::setName);
        Updater.updateField(log, existing.getId(), FILM, "description", request.getDescription(),
                existing.getDescription(), existing::setDescription);
        Updater.updateField(log, existing.getId(), FILM, "releaseDate", request.getReleaseDate(),
                existing.getReleaseDate(), existing::setReleaseDate);
        Updater.updateField(log, existing.getId(), FILM, "duration", request.getDuration(),
                existing.getDuration(), existing::setDuration);

        return FilmMapper.toDto(filmRepository.update(existing));
    }

    public void addLike(Long filmId, Long userId) {
        getById(filmId);
        isUserNotFound(userId);
        if (!likeStorage.addLike(filmId, userId)) {
            throw new DuplicateException("Пользователю уже понравился этот фильм");
        }
        log.debug("Пользователь ID {} удалил лайк фильма ID {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        getById(filmId);
        isUserNotFound(userId);
        if (!likeStorage.deleteLike(filmId, userId)) {
            throw new DuplicateException("Данного лайка не существует");
        }
    }

    public List<FilmResponse> getPopular(Integer count) {
        log.info("Получаем {} самых популярных фильмов", count);
        return filmRepository.getPopular(count).stream()
                .map(FilmMapper::toDto)
                .collect(Collectors.toList());
    }

    private void isUserNotFound(Long userId) {
        if (!userRepository.exists(userId)) {
            log.warn("Пользователь с ID {} не существует", userId);
            throw new NotFoundException("Пользователь c ID " + userId + " не существует!");
        }
    }

    public Film getById(Long id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Фильм не найден. ID: {}", id);
                    return new NotFoundException(
                            "Фильм ID: = " + id + " не найден");
                });
    }
}
