package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewsNotFoundException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.review.ReviewStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewStorage storage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedService feedService;

    public ReviewService(ReviewStorage storage, UserStorage userStorage, FilmStorage filmStorage, FeedService feedService) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.feedService = feedService;
    }

    public Review create(Review review) {
        requireUser(review.getUserId());
        requireFilm(review.getFilmId());
        review.setReviewId(null);
        review.setUseful(0);

        Review createdReview = storage.create(review);

        feedService.saveEvent(createdReview.getUserId(), createdReview.getReviewId(), EventType.REVIEW, EventOperation.ADD);

        return createdReview;
    }

    public Review update(Review review) {
        if (review.getReviewId() == null) {
            throw new ReviewsNotFoundException("Не указан идентификатор отзыва");
        }
        requireReview(review.getReviewId());

        Review updatedReview = storage.update(review);
        feedService.saveEvent(updatedReview.getUserId(), updatedReview.getReviewId(), EventType.REVIEW, EventOperation.UPDATE);

        return updatedReview;
    }

    public void delete(long reviewId) {
        Review deletedReview = requireReview(reviewId);
        storage.delete(reviewId);
        feedService.saveEvent(deletedReview.getUserId(), reviewId, EventType.REVIEW, EventOperation.REMOVE);
    }

    public Review findById(long reviewId) {
        return requireReview(reviewId);
    }

    public List<Review> findAll(Long filmId, int count) {
        if (filmId != null) {
            requireFilm(filmId);
        }
        return storage.findAll(filmId == null ? 0 : filmId, count);
    }

    public Review addLike(long reviewId, long userId) {
        requireReview(reviewId);
        requireUser(userId);
        storage.setReaction(reviewId, userId, true);
        return requireReview(reviewId);
    }

    public Review addDislike(long reviewId, long userId) {
        requireReview(reviewId);
        requireUser(userId);
        storage.setReaction(reviewId, userId, false);
        return requireReview(reviewId);
    }

    public Review removeLike(long reviewId, long userId) {
        return removeReaction(reviewId, userId, true);
    }

    public Review removeDislike(long reviewId, long userId) {
        return removeReaction(reviewId, userId, false);
    }

    private Review removeReaction(long reviewId, long userId, boolean useful) {
        requireReview(reviewId);
        requireUser(userId);
        storage.deleteReaction(reviewId, userId, useful);
        return requireReview(reviewId);
    }

    private void requireUser(long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
    }

    private void requireFilm(long filmId) {
        filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID: " + filmId + " не найден"));
    }

    private Review requireReview(long reviewId) {
        return storage.findById(reviewId)
                .orElseThrow(() -> new ReviewsNotFoundException("Отзыв с ID: " + reviewId + " не найден"));
    }
}

