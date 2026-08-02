package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.director.DirectorResponse;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.Director;
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
    private final FeedService feedService;
    private final DirectorService directorService;

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

        List<Long> directorIds = request.getDirectors() != null
                ? request.getDirectors().stream().map(DirectorResponse::getId).distinct().toList()
                : List.of();
        directorService.updateFilmDirectors(film.getId(), directorIds);

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

        List<Long> directorIds = request.getDirectors() != null
                ? request.getDirectors().stream().map(DirectorResponse::getId).distinct().toList()
                : List.of();
        directorService.updateFilmDirectors(film.getId(), directorIds);

        log.info("Обновлен фильм {} с ID {}", filmUpdate.getName(), filmUpdate.getId());
        return buildFilmResponse(filmUpdate);
    }

    public void delete(long id) {
        findById(id);
        filmRepository.delete(id);
        log.info("Удален фильм ID {}", id);
    }


    public List<FilmResponse> getPopular(Integer count, Long genreId, Integer year) {
        log.info("Получаем {} популярных фильмов (жанр: {}, год: {})", count, genreId, year);
        if (count == null) {
            count = (genreId != null || year != null) ? Integer.MAX_VALUE : 10;
        }
        List<Film> films = filmRepository.getPopular(count, genreId, year);
        return buildFilmResponses(films);
    }

    public void addLike(Long filmId, Long userId) {
        filmIsExists(filmId);
        userIsExists(userId);
        feedService.saveEvent(userId, filmId, EventType.LIKE, EventOperation.ADD);

        boolean isAdded = likeRepository.addLike(filmId, userId);

        if (!isAdded) {
            log.info("Пользователь ID {} уже ставил лайк фильму ID {}", userId, filmId);
            return;
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

        feedService.saveEvent(userId, filmId, EventType.LIKE, EventOperation.REMOVE);
    }

    public List<FilmResponse> findDirectorFilms(long directorId, String sortType) {
        directorService.findById(directorId);
        List<Film> films = filmRepository.findDirectorFilms(directorId, sortType);
        log.debug("Found {} films", films.size());
        return buildFilmResponses(films);
    }

    public List<FilmResponse> buildFilmResponses(Collection<Film> films) {
        if (films.isEmpty()) {
            return List.of();
        }

        Set<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toSet());
        Map<Long, List<Genre>> genresMap = genreService.findGenresByFilmIds(filmIds);
        Map<Long, List<Director>> directorsMap = directorService.findDirectorsByFilmIds(filmIds);

        return films.stream()
                .map(film -> {
                    FilmResponse response = FilmMapper.toDto(film);
                    response.setGenres(genresMap.getOrDefault(film.getId(), List.of()));
                    response.setMpa(film.getMpa());
                    response.setDirectors(directorsMap.getOrDefault(film.getId(), List.of()));
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

    public List<FilmResponse> getCommonFilms(Long userId, Long friendId) {
        userIsExists(userId);
        userIsExists(friendId);
        log.info("Запрос списка общих фильмов пользователей ID: {} | {}", userId, friendId);

        List<Film> commonFilms = filmRepository.getCommonFilms(userId, friendId);
        return buildFilmResponses(commonFilms);
    }

    public List<FilmResponse> search(String query, Set<String> by) {
        log.debug("Поиск фильмов, запрос: '{}', фильтры: '{}'", query, by);
        Set<String> collect = by.stream().map(String::toLowerCase).collect(Collectors.toSet());
        List<Film> films = filmRepository.search(query, collect);
        return buildFilmResponses(films);
    }
}