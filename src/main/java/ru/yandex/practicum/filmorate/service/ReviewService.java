package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.exception.ReviewsNotFoundException;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.review.ReviewStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

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

    public Review create(Review review) {
        requireUser(review.getUserId());
        requireFilm(review.getFilmID());
        review.setReviewId(0);
        review.setUseful(0);
        return storage.create(review);
    }

    public Review update(Review review) {
       if (review.getReviewId() == 0) {
           throw new ReviewsNotFoundException("Не указан идентификатор отзыва");
       }
       requireReview(review.getReviewId());
       return storage.update(review);
    }

    public void delete(long reviewId) {
        requireReview(reviewId);
        storage.delete(reviewId);

    }

    public Review findById(long reviewId) {
        return requireReview(reviewId);
    }

    public List<Review> findAll(long filmId, int count) {
        if (filmId == 0) {
            requireFilm(filmId);
        }
        return storage.findAll(filmId, count);
    }

    public void addLike(long reviewId, long userid) {
        requireReview(reviewId);
        requireUser(userid);
        storage.setReaction(reviewId, userid, true);
    }

    public void addDislike(long reviewId, long userid) {
        requireReview(reviewId);
        requireUser(userid);
        storage.setReaction(reviewId, userid, false);
    }

    public void removeLike(long reviewId, long userId){
        removeReaction(reviewId, userId, true);
    }

    public void removeDislike(long reviewId, long userId) {
        removeReaction(reviewId, userId, false);
    }

    private void removeReaction(long reviewId, long userId, boolean useful) {
        requireReview(reviewId);
        requireUser(userId);

        if(!storage.deleteReaction(reviewId, userId, useful)) {
            throw new ReviewsNotFoundException("Оценка отзыва не найдена");
        }

    }


    private void requireUser(long userId) {
        userStorage.findById(userId).orElseThrow(()-> new RuntimeException("Пользователь не найден"));

    }

    private void requireFilm(long filmId) {
        filmStorage.findById(filmId).orElseThrow(()-> new RuntimeException("Фильм не найден"));

    }

    private Review requireReview(long reviewId) {
       return storage.findById(reviewId).orElseThrow(()-> new RuntimeException("Отзыв не найден"));
    }


}


