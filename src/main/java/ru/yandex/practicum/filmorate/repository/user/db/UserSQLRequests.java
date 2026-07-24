package ru.yandex.practicum.filmorate.repository.user.db;

import lombok.NoArgsConstructor;

/**
 * Основные SQL запросы к фильмам.
 */
@NoArgsConstructor
public class UserSQLRequests {

    public static final String FIND_ALL_USERS = """
            SELECT *
            FROM users
            """;

    public static final String FIND_USER_BY_ID = """
            SELECT *
            FROM users
            WHERE id = ?
            """;

    public static final String FIND_USERS_BY_IDS = """
            SELECT *
            FROM users
            WHERE id IN (:ids)
            """;

    public static final String INSERT_USER = """
            INSERT INTO users(email, login, name, birthday)
            VALUES (?, ?, ?, ?)
            """;

    public static final String UPDATE_USER = """
            UPDATE users SET email = ?, login = ?, name = ?, birthday = ?
            WHERE id = ?
            """;

    public static final String DELETE_USER = """
            DELETE FROM users
            WHERE id = ?
            """;
}
