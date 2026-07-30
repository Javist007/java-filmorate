package ru.yandex.practicum.filmorate.service.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
@Component
public class ReviewRowMapper implements RowMapper<Review> {
    public Review mapRow(ResultSet rs, int rowNumber) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getLong("review_id"));
        review.setContent(rs.getString("content"));
        review.setPositive(rs.getBoolean("positive"));
        review.setUserId(rs.getLong("user_id"));
        review.setFilmID(rs.getLong("film_id"));
        review.setUseful(rs.getInt("useful"));
        return review;
    }

}
