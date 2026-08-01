package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.CreateReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewResponse;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@Validated
public class ReviewsController {
    private final ReviewService reviewService;

    public ReviewsController(ReviewService service) {
        this.reviewService = service;
    }

    @PostMapping
    public ReviewResponse create(@Valid @RequestBody CreateReviewRequest request) {
        return reviewService.create(request);
    }

    @PutMapping
    public ReviewResponse update(@Valid @RequestBody UpdateReviewRequest request) {
        return reviewService.update(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long reviewId) {
        reviewService.delete(reviewId);
    }

    @GetMapping("/{id}")
    public ReviewResponse findById(@PathVariable long id) {
        return reviewService.findById(id);
    }

    @GetMapping
    public List<ReviewResponse> findAll(@RequestParam(required = false) Long filmId,
                                       @RequestParam(defaultValue = "10") @Positive int count) {
        return reviewService.findAll(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewResponse addLike(@PathVariable long id, @PathVariable long userId) {
        return reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewResponse addDislike(@PathVariable long id, @PathVariable long userId) {
        return reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ReviewResponse removeLike(@PathVariable long id, @PathVariable long userId) {
        return reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewResponse removeDislike(@PathVariable long id, @PathVariable long userId) {
        return reviewService.removeDislike(id, userId);
    }
}
