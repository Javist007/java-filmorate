package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmRequest;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.mapper.FilmMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST‑контроллер для фильмов.
 */
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmResponse> findAll() {
        log.info("GET /films – получение списка всех фильмов");
        return filmService.findAll().stream()
                .map(FilmMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public FilmResponse create(@Valid @RequestBody FilmRequest request) {
        log.info("POST /films – создание фильма: {}", request.getName());
        Film created = filmService.create(FilmMapper.toEntity(request));
        return FilmMapper.toDto(created);
    }

    @PutMapping
    public FilmResponse update(@Valid @RequestBody FilmRequest request) {
        log.info("PUT /films – обновление фильма ID={}", request.getId());
        Film updated = filmService.update(FilmMapper.toEntity(request));
        return FilmMapper.toDto(updated);
    }

    @GetMapping("/{id}")
    public FilmResponse getFilm(@PathVariable Long id) {
        log.info("GET /films/{} – получение конкретного фильма", id);
        Film film = filmService.getById(id);
        return FilmMapper.toDto(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("PUT /films/{}/like/{} – пользователь {} ставит лайк фильму {}", id, userId, userId, id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("DELETE /films/{}/like/{} – пользователь {} снимает лайк фильму {}", id, userId, userId, id);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmResponse> getPopular(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("GET /films/popular?count={}", count);
        return filmService.getPopular(count).stream()
                .map(FilmMapper::toDto)
                .collect(Collectors.toList());
    }
}
