package ru.yandex.practicum.filmorate.repository.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Review;
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
        long id = insert(ReviewSQLRequests.INSERT, review.getContent(), review.isPositive(), review.getUserId(), review.getFilmID());
        review.setReviewId(id);
        review.setUseful(0);
        return review;
    }

    @Override
    public Review update(Review review) {
        update(ReviewSQLRequests.UPDATE, review.getContent(), review.isPositive(), review.getReviewId());
        return findById(review.getReviewId()).orElseThrow();
    }

    @Override
    public boolean delete(long reviewId) {
       return delete(ReviewSQLRequests.DELETE, reviewId);
    }

    @Override
    public Optional<Review> findById(long reviewId) {
        return findOne(ReviewSQLRequests.FIND_BY_ID, reviewId);
    }

    @Override
    public List<Review> findAll(long filmId, int count) {
        if (filmId == 0) {
            return findMany(ReviewSQLRequests.FIND_ALL, count);
        } else {
            return findMany(ReviewSQLRequests.FIND_BY_FILM_ID, count);
        }
    }

    @Override
    public void setReaction(long reviewId, long userId, boolean useful) {
        List<Boolean> existing = jdbc.query(
                "SELECT is_useful FROM review_reactions WHERE review_id = ? AND user_id = ?",
                (rs, rowNum) -> rs.getBoolean("useful"),
                reviewId,
                userId);
        if (existing.isEmpty()) {
            jdbc.update("""
                    INSERT INTO review_reactions (review_id, user_id, is_useful)
                    VALUES (?, ?, ?)
                    """, reviewId, userId, useful);
        } else if (existing.getFirst() != useful) {
            jdbc.update("""
                    UPDATE review_reactions SET is_useful = ?
                    WHERE review_id = ? AND user_id = ?
                    """, useful, reviewId, userId);
        }
    }

    @Override
    public boolean deleteReaction(long reviewId, long userId, boolean useful) {
        return jdbc.update("""
                DELETE FROM review_reactions
                WHERE review_id = ? AND user_id = ? AND is_useful = ?
                """, reviewId, userId, useful) > 0;
    }
}
