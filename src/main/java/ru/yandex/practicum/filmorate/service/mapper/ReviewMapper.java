package ru.yandex.practicum.filmorate.service.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.review.CreateReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewResponse;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.entity.Review;

@UtilityClass
public class ReviewMapper {
    public Review toEntity(CreateReviewRequest request) {
        Review review = new Review();
        review.setContent(request.getContent());
        review.setIsPositive(request.getIsPositive());
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());
        return review;
    }

    public Review toEntity(UpdateReviewRequest request) {
        Review review = new Review();
        review.setReviewId(request.getReviewId());
        review.setContent(request.getContent());
        review.setIsPositive(request.getIsPositive());
        return review;
    }

    public ReviewResponse toDto(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setReviewId(review.getReviewId());
        response.setContent(review.getContent());
        response.setIsPositive(review.getIsPositive());
        response.setUserId(review.getUserId());
        response.setFilmId(review.getFilmId());
        response.setUseful(review.getUseful());
        return response;
    }
}
