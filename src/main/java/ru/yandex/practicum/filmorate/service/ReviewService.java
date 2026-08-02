package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.review.CreateReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewResponse;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewsNotFoundException;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.review.ReviewStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;
import ru.yandex.practicum.filmorate.service.mapper.ReviewMapper;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewStorage storage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public ReviewService(ReviewStorage storage, UserStorage userStorage, FilmStorage filmStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public ReviewResponse create(CreateReviewRequest request) {
        Review review = ReviewMapper.toEntity(request);
        requireUser(review.getUserId());
        requireFilm(review.getFilmId());
        return ReviewMapper.toDto(storage.create(review));
    }

    public ReviewResponse update(UpdateReviewRequest request) {
        requireReview(request.getReviewId());
        return ReviewMapper.toDto(storage.update(ReviewMapper.toEntity(request)));
    }

    public void delete(long reviewId) {
        requireReview(reviewId);
        storage.delete(reviewId);
    }

    public ReviewResponse findById(long reviewId) {
        return ReviewMapper.toDto(requireReview(reviewId));
    }

    public List<ReviewResponse> findAll(Long filmId, int count) {
        if (filmId != null) {
            requireFilm(filmId);
        }
        return storage.findAll(filmId == null ? 0 : filmId, count).stream()
                .map(ReviewMapper::toDto)
                .toList();
    }

    public ReviewResponse addLike(long reviewId, long userId) {
        requireReview(reviewId);
        requireUser(userId);
        storage.setReaction(reviewId, userId, true);
        return ReviewMapper.toDto(requireReview(reviewId));
    }

    public ReviewResponse addDislike(long reviewId, long userId) {
        requireReview(reviewId);
        requireUser(userId);
        storage.setReaction(reviewId, userId, false);
        return ReviewMapper.toDto(requireReview(reviewId));
    }

    public ReviewResponse removeLike(long reviewId, long userId) {
        return removeReaction(reviewId, userId, true);
    }

    public ReviewResponse removeDislike(long reviewId, long userId) {
        return removeReaction(reviewId, userId, false);
    }

    private ReviewResponse removeReaction(long reviewId, long userId, boolean useful) {
        requireReview(reviewId);
        requireUser(userId);
        storage.deleteReaction(reviewId, userId, useful);
        return ReviewMapper.toDto(requireReview(reviewId));
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
