package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmResponse getFilm(@PathVariable Long id) {
        log.info("GET /films/{} – получение конкретного фильма ", id);
        return filmService.findById(id);
    }

    @GetMapping("/common")
    public Collection<FilmResponse> getCommonFilms(
            @RequestParam Long userId,
            @RequestParam Long friendId
    ) {
        log.info("GET /films/common?userId={}&friendId={} – запрос общих фильмов", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @PostMapping
    public FilmResponse create(@Valid @RequestBody CreateFilmRequest request) {
        log.info("POST /films – создание фильма: {}", request.getName());
        return filmService.create(request);
    }

    @PutMapping
    public FilmResponse update(@Valid @RequestBody UpdateFilmRequest request) {
        log.info("PUT /films – обновление фильма ID={}", request.getId());
        return filmService.update(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive long id) {
        log.info("DELETE /films - удаление фильма ID={}", id);
        filmService.delete(id);
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
    public List<FilmResponse> getPopular(
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) @Min(1895) Integer year
        ) {
        log.info("GET /films/popular?count={}&genreId={}&year={}", count, genreId, year);
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("director/{directorId}")
    public List<FilmResponse> getDirectorFilmsSorted(@PathVariable @Positive long directorId,
                                                     @RequestParam(name = "sortBy") String sortType) {
        List<FilmResponse> films = filmService.findDirectorFilms(directorId, sortType);
        log.debug("Найдено {} фильмов", films.size());
        return films;
    }

    @GetMapping("/search")
    public List<FilmResponse> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "title") Set<String> by
    ) {
        List<FilmResponse> results = filmService.search(query, by);
        log.debug("{} фильмов найдено", results.size());
        return results;
    }
}