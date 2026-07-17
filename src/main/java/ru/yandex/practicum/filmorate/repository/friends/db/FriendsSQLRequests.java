package ru.yandex.practicum.filmorate.repository.friends.db;

import lombok.NoArgsConstructor;

/**
 * Основные SQL запросы к фильмам.
 */
@NoArgsConstructor
public class FriendsSQLRequests {

    public static final String FIND_FRIEND = """
            SELECT friend_id
            FROM friends
            WHERE user_id = ?
            """;

    public static final String ADD_FRIEND = """
            MERGE INTO friends (user_id, friend_id)
            VALUES (?, ?)
            """;

    public static final String REMOVE_FRIEND = """
            DELETE FROM friends
            WHERE user_id = ? AND friend_id = ?
            """;

    public static final String FIND_COMMON_FRIENDS = """
            SELECT f1.friend_id
            FROM friends f1
            INNER JOIN friends f2 ON f1.friend_id = f2.friend_id
            WHERE f1.user_id = ? AND f2.user_id = ?
            """;
}
