package ru.yandex.practicum.filmorate.repository.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.BaseStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewDBRepository extends BaseStorage<Review> implements ReviewStorage {
    public ReviewDBRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review create(Review review) {
        long id = insert(
                ReviewSqlRequest.INSERT,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId()
        );
        review.setReviewId(id);
        review.setUseful(0);
        return review;
    }

    @Override
    public Review update(Review review) {
        update(ReviewSqlRequest.UPDATE, review.getContent(), review.getIsPositive(), review.getReviewId());
        return findById(review.getReviewId()).orElseThrow();
    }

    @Override
    public boolean delete(long reviewId) {
        return delete(ReviewSqlRequest.DELETE, reviewId);
    }

    @Override
    public Optional<Review> findById(long reviewId) {
        return findOne(ReviewSqlRequest.FIND_BY_ID, reviewId);
    }

    @Override
    public List<Review> findAll(long filmId, int count) {
        if (filmId == 0) {
            return findMany(ReviewSqlRequest.FIND_ALL, count);
        } else {
            return findMany(ReviewSqlRequest.FIND_BY_FILM_ID, filmId, count);
        }
    }

    @Override
    public void setReaction(long reviewId, long userId, boolean useful) {
        List<Boolean> existing = jdbc.query(
                ReviewSqlRequest.FIND_REACTION,
                (rs, rowNum) -> rs.getBoolean("is_useful"),
                reviewId,
                userId);
        if (existing.isEmpty()) {
            jdbc.update(ReviewSqlRequest.INSERT_REACTION, reviewId, userId, useful);
        } else if (existing.getFirst() != useful) {
            jdbc.update(ReviewSqlRequest.UPDATE_REACTION, useful, reviewId, userId);
        }
    }

    @Override
    public void deleteReaction(long reviewId, long userId, boolean useful) {
        jdbc.update(ReviewSqlRequest.DELETE_REACTION, reviewId, userId, useful);
    }
}
