package ru.yandex.practicum.filmorate.repository.events;

import lombok.NoArgsConstructor;

/**
 * SQL запрос на ленту событий.
 */
@NoArgsConstructor
public class EventSQLRequests {

    public static final String FIND_BY_USER_ID = """
            SELECT *
            FROM events
            WHERE user_id = ?
            ORDER BY event_timestamp DESC"
            """;

    public static final String INSERT_EVENT = """
            INSERT INTO events (user_id, entity_id, event_type, operation, event_timestamp)
            VALUES (?, ?, ?, ?, ?)
            """;
}
