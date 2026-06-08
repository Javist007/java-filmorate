package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import ru.yandex.practicum.filmorate.validation.MinReleaseDate;

import java.time.LocalDate;

/**
 * Модель фильма.
 */
@Data
@Builder
public class Film {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    @MinReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;
}
