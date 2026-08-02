package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.review.CreateReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewResponse;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewsNotFoundException;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.review.ReviewStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;
import ru.yandex.practicum.filmorate.service.mapper.ReviewMapper;

import java.util.List;

/**
 * Бизнес‑слой для отзывов.
 */
@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage storage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedService feedService;
    private static final long NO_FILM_FILTER_ID = 0L;

    public ReviewService(ReviewStorage storage, UserStorage userStorage, FilmStorage filmStorage, FeedService feedService) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.feedService = feedService;
    }

    public ReviewResponse create(CreateReviewRequest request) {
        log.debug("Создаём отзыв на фильм ID: {} от пользователя ID: {}", request.getFilmId(), request.getUserId());
        Review review = ReviewMapper.toEntity(request);
        requireUser(review.getUserId());
        requireFilm(review.getFilmId());

        Review createdReview = storage.create(review);

        feedService.saveEvent(createdReview.getUserId(), createdReview.getReviewId(), EventType.REVIEW, EventOperation.ADD);

        return ReviewMapper.toDto(createdReview);
    }

    public ReviewResponse update(UpdateReviewRequest request) {
        if (request.getReviewId() == null) {
            throw new ReviewsNotFoundException("Не указан идентификатор отзыва");
        }
        requireReview(request.getReviewId());
        log.debug("Обновляем отзыв ID {}", request.getReviewId());

        Review updatedReview = storage.update(ReviewMapper.toEntity(request));

        feedService.saveEvent(updatedReview.getUserId(), updatedReview.getReviewId(), EventType.REVIEW, EventOperation.UPDATE);

        return ReviewMapper.toDto(updatedReview);
    }

    public void delete(long reviewId) {
        Review deletedReview = requireReview(reviewId);
        log.debug("Удаляем отзыв ID {}", reviewId);

        storage.delete(reviewId);

        feedService.saveEvent(deletedReview.getUserId(), reviewId, EventType.REVIEW, EventOperation.REMOVE);
        log.info("Удалён отзыв ID {}", reviewId);
    }

    public ReviewResponse findById(long reviewId) {
        log.debug("Получение отзыва по ID: {}", reviewId);
        return ReviewMapper.toDto(requireReview(reviewId));
    }

    public List<ReviewResponse> findAll(Long filmId, int count) {
        log.debug("Получаем список отзывов (фильтр по фильму ID: {}, лимит: {})", filmId, count);
        if (filmId != null) {
            requireFilm(filmId);
        }

        long targetFilmId = (filmId == null) ? NO_FILM_FILTER_ID : filmId;

        return storage.findAll(targetFilmId, count).stream()
                .map(ReviewMapper::toDto)
                .toList();
    }

    public ReviewResponse addLike(long reviewId, long userId) {
        requireReview(reviewId);
        requireUser(userId);
        log.debug("Добавление лайка к отзыву ID: {} от пользователя ID: {}", reviewId, userId);
        storage.setReaction(reviewId, userId, true);
        return ReviewMapper.toDto(requireReview(reviewId));
    }

    public ReviewResponse addDislike(long reviewId, long userId) {
        requireReview(reviewId);
        requireUser(userId);
        log.debug("Добавление дизлайка к отзыву ID: {} от пользователя ID: {}", reviewId, userId);
        storage.setReaction(reviewId, userId, false);
        return ReviewMapper.toDto(requireReview(reviewId));
    }

    public ReviewResponse removeLike(long reviewId, long userId) {
        log.debug("Удаление лайка с отзыва ID: {} от пользователя ID: {}", reviewId, userId);
        return removeReaction(reviewId, userId, true);
    }

    public ReviewResponse removeDislike(long reviewId, long userId) {
        log.debug("Удаление дизлайка с отзыва ID: {} от пользователя ID: {}", reviewId, userId);
        return removeReaction(reviewId, userId, false);
    }

    private ReviewResponse removeReaction(long reviewId, long userId, boolean useful) {
        requireReview(reviewId);
        requireUser(userId);
        storage.deleteReaction(reviewId, userId, useful);
        return ReviewMapper.toDto(requireReview(reviewId));
    }

    private void requireUser(long userId) {
        log.debug("Проверка существования пользователя ID: {}", userId);
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
    }

    private void requireFilm(long filmId) {
        log.debug("Проверка существования фильма ID: {}", filmId);
        filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID: " + filmId + " не найден"));
    }

    private Review requireReview(long reviewId) {
        log.debug("Проверка существования отзыва ID: {}", reviewId);
        return storage.findById(reviewId)
                .orElseThrow(() -> new ReviewsNotFoundException("Отзыв с ID: " + reviewId + " не найден"));
    }
}
