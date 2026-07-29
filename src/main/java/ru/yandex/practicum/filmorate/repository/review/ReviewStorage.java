package ru.yandex.practicum.filmorate.repository.review;

import ru.yandex.practicum.filmorate.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review create(Review review);
    Review update(Review review);
    boolean delete(long reviewId);
    Optional<Review> findById(long reviewId);
    List<Review> findAll(long filmId, int count);
    void setReaction(long reviewId, long userId, boolean useful);
    boolean deleteReaction(long reviewId, long userId, boolean useful);
}
