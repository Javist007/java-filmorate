package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Входной DTO для создания режиссёра.
 */
@Data
public class CreateDirectorRequest {
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
}
