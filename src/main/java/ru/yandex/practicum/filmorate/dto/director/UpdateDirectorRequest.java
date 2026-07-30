package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Входной DTO для обновления режиссёра.
 */
@Data
public class UpdateDirectorRequest {
    @NotNull(message = "ID должен быть указан")
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
}
