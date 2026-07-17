package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Лайки пользователей к фильмам.
 */
@Data
@RequiredArgsConstructor
public class Like {
    private final Long filmId;
    private final Long userId;
}
