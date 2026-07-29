package ru.yandex.practicum.filmorate.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jdk.jfr.Enabled;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
 public class Review {


    private long reviewId;
    @NotBlank(message = "Отзыв не может быть пустым")
    private String content;
    @NotNull(message = "Тип отзыва должен быть указан")
    private boolean positive;
    @NotNull
    @Positive
    private long userId;
    @NotNull
    @Positive
    private long filmID;

    private int useful = 0;
}
