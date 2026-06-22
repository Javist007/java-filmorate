package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

/**
 * Выходной DTO для фильма.
 */
@Data
public class FilmResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Long> likedBy = new java.util.HashSet<>();
}
