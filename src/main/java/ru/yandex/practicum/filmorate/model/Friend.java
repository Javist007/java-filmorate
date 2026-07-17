package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Дружественные связи пользователей.
 */
@Data
@RequiredArgsConstructor
public class Friend {
    private final long userId;
    private final long friendId;
}
