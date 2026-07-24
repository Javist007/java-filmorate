package ru.yandex.practicum.filmorate.repository.like.db;

import lombok.NoArgsConstructor;

/**
 * Основные SQL запросы к лайкам фильмов.
 */
@NoArgsConstructor
public class LikeSQLRequests {

    public static final String FIND_LIKES_BY_FILM_ID = """
            SELECT *
            FROM likes
            WHERE film_id = ?
            """;

    public static final String INSERT_LIKE = """
            INSERT INTO likes(film_id, user_id)
            VALUES(?, ?)
            """;

    public static final String DELETE_LIKE = """
            DELETE
            FROM likes
            WHERE film_id = ? and user_id = ?
            """;
}
