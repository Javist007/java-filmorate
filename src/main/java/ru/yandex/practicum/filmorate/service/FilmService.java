package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.like.LikeStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;
import ru.yandex.practicum.filmorate.service.mapper.FilmMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Бизнес‑слой для фильмов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmRepository;
    private final UserStorage userRepository;
    private final LikeStorage likeRepository;
    private final GenreService genreService;
    private final MpaService mpaService;

    public Collection<FilmResponse> findAll() {
        log.info("Получаем список всех фильмов");
        Collection<Film> films = filmRepository.findAll();
        return buildFilmResponses(films);
    }

    public FilmResponse findById(Long id) {
        return filmRepository.findById(id)
                .map(this::buildFilmResponse)
                .orElseThrow(() -> new NotFoundException("Фильм ID: " + id + " не найден"));
    }

    public FilmResponse create(@Valid CreateFilmRequest request) {
        if (request.getMpa() != null) {
            mpaService.findById(request.getMpa().getId());
        }
        Film film = FilmMapper.toEntity(request);
        Film newFilm = filmRepository.create(film);
        List<Long> genreIds = request.getGenres() != null
                ? request.getGenres().stream().map(GenreDto::getId).distinct().toList()
                : null;
        genreService.updateFilmGenres(film.getId(), genreIds);
        log.info("Добавлен фильм {} с ID {}", newFilm.getName(), newFilm.getId());
        return buildFilmResponse(newFilm);
    }

    public FilmResponse update(UpdateFilmRequest request) {
        findById(request.getId());
        Film film = FilmMapper.toEntity(request);
        Film filmUpdate = filmRepository.update(film);
        List<Long> genreIds = request.getGenres() != null
                ? request.getGenres().stream().map(GenreDto::getId).distinct().toList()
                : null;
        genreService.updateFilmGenres(film.getId(), genreIds);
        log.info("Обновлен фильм {} с ID {}", filmUpdate.getName(), filmUpdate.getId());
        return buildFilmResponse(filmUpdate);
    }

    public void delete(long id) {
        findById(id);
        filmRepository.delete(id);
        log.info("Удален фильм ID {}", id);
    }


    public List<FilmResponse> getPopular(Integer count) {
        log.info("Получаем {} самых популярных фильмов", count);
        List<Film> films = filmRepository.getPopular(count);
        return buildFilmResponses(films);
    }

    public void addLike(Long filmId, Long userId) {
        filmIsExists(filmId);
        userIsExists(userId);

        if (!likeRepository.addLike(filmId, userId)) {
            throw new DuplicateException("Пользователю уже понравился этот фильм");
        }
        log.debug("Пользователь ID {} поставил лайк фильму ID {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmIsExists(filmId);
        userIsExists(userId);

        if (!likeRepository.deleteLike(filmId, userId)) {
            throw new NotFoundException("Данного лайка не существует");
        }
        log.debug("Пользователь ID {} убрал лайк с фильма ID {}", userId, filmId);
    }

    private List<FilmResponse> buildFilmResponses(Collection<Film> films) {
        if (films.isEmpty()) {
            return List.of();
        }

        Set<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toSet());
        Map<Long, List<Genre>> genresMap = genreService.findGenresByFilmIds(filmIds);

        return films.stream()
                .map(film -> {
                    FilmResponse response = FilmMapper.toDto(film);
                    response.setGenres(genresMap.getOrDefault(film.getId(), List.of()));
                    response.setMpa(film.getMpa());
                    return response;
                })
                .toList();
    }

    private FilmResponse buildFilmResponse(Film film) {
        return buildFilmResponses(List.of(film)).getFirst();
    }

    private void filmIsExists(Long filmId) {
        filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм ID: " + filmId + " не найден"));
    }

    private void userIsExists(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь ID: " + userId + " не найден"));
    }
}