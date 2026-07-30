package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Review;
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
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@Valid @PathVariable long review) {
        reviewService.delete(review);
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable long id) {
        return reviewService.findById(id);
    }

    @GetMapping()
    public List<Review> findAll(@RequestParam(required = false) @Positive long id, @RequestParam(defaultValue = "10") int count) {
        return reviewService.findAll(id, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable @Positive long id, @PathVariable @Positive long userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable @Positive long id, @PathVariable @Positive long userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable @Positive long id, @PathVariable @Positive long userId) {
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable @Positive long id, @PathVariable @Positive long userId) {
        reviewService.removeDislike(id, userId);
    }
}
