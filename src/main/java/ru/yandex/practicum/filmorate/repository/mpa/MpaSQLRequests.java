package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.NoArgsConstructor;

/**
 * Основные SQL запросы к рейтингам фильмов.
 */
@NoArgsConstructor
public class MpaSQLRequests {

    public static final String FIND_ALL_MPA = "SELECT * FROM ratings";

    public static final String FIND_MPA_BY_ID = "SELECT * FROM ratings where id = ?";
}
