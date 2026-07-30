package ru.yandex.practicum.filmorate.dto.director;

import lombok.Data;

/**
 * Выходной DTO для режиссёра.
 */
@Data
public class DirectorResponse {
    private Long id;
    private String name;
}
