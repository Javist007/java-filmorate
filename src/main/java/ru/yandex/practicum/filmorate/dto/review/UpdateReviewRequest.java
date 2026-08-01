package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReviewRequest {
    @NotNull(message = "ID отзыва должен быть указан")
    private Long reviewId;

    @NotBlank(message = "Отзыв не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва должен быть указан")
    private Boolean isPositive;
}
