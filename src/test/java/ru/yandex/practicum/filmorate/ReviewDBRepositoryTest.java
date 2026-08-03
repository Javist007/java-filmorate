package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.review.ReviewDBRepository;
import ru.yandex.practicum.filmorate.repository.review.mapper.ReviewRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ReviewDBRepository.class, ReviewRowMapper.class, TestEntity.class})
class ReviewDBRepositoryTest {

    private final ReviewDBRepository reviewRepository;
    private final TestEntity testEntity;

    private long filmId;
    private long authorId;
    private long voterId;

    @BeforeEach
    void setUp() {
        testEntity.cleanTables("review_reactions", "reviews", "film_genre", "likes", "films", "users");
        filmId = testEntity.insertFilm("Film", LocalDate.of(2020, 1, 1), 100, 1);
        authorId = testEntity.insertUser("author@example.com", "author");
        voterId = testEntity.insertUser("voter@example.com", "voter");
    }

    @Test
    void createHasZeroUsefulRating() {
        Review created = reviewRepository.create(review("Content"));

        assertThat(created.getReviewId()).isPositive();
        assertThat(reviewRepository.findById(created.getReviewId()))
                .hasValueSatisfying(review -> assertThat(review.getUseful()).isZero());
    }

    @Test
    void reactionsChangeRatingAndOrder() {
        Review first = reviewRepository.create(review("First"));
        Review second = reviewRepository.create(review("Second"));

        reviewRepository.setReaction(second.getReviewId(), voterId, true);
        List<Review> reviews = reviewRepository.findAll(filmId, 10);

        assertThat(reviews).extracting(Review::getReviewId)
                .containsExactly(second.getReviewId(), first.getReviewId());
        assertThat(reviews.getFirst().getUseful()).isEqualTo(1);

        reviewRepository.setReaction(second.getReviewId(), voterId, false);
        assertThat(reviewRepository.findById(second.getReviewId()))
                .hasValueSatisfying(review -> assertThat(review.getUseful()).isEqualTo(-1));
    }

    private Review review(String content) {
        Review review = new Review();
        review.setContent(content);
        review.setIsPositive(true);
        review.setUserId(authorId);
        review.setFilmId(filmId);
        return review;
    }
}