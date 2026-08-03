package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.recommendation.RecommendationStorage;

import java.util.List;

/**
 * Рекомендации.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationStorage recommendationStorage;
    private final UserService userService;
    private final FilmService filmService;

    public List<FilmResponse> findRecommendations(Long userId) {
        log.debug("Поиск рекомендованных фильмов для пользователя ID: {}", userId);

        userService.isExists(userId);

        List<Film> recommendedFilms = recommendationStorage.findRecommendedFilms(userId);
        if (recommendedFilms.isEmpty()) {
            return List.of();
        }
        return filmService.buildFilmResponses(recommendedFilms);
    }
}
