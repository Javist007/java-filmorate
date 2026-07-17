package ru.yandex.practicum.filmorate.model;

import lombok.Data;

/**
 * Жанры фильмов.
 */
@Data
public class Genre {
    private long id;
    private String name;
}
