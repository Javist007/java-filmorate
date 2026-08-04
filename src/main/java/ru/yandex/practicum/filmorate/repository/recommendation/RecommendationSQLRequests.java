package ru.yandex.practicum.filmorate.repository.recommendation;

import lombok.NoArgsConstructor;

/**
 * Основные SQL запросы для рекомендаций.
 */
@NoArgsConstructor
public class RecommendationSQLRequests {

    public static final String FIND_RECOMMEND_FILM = """
            WITH similar_user AS (
                SELECT l2.user_id, COUNT(*) AS common_cnt
                FROM likes l1
                JOIN likes l2 ON l1.film_id = l2.film_id
                WHERE l1.user_id = :user_id AND l2.user_id <> :user_id
                GROUP BY l2.user_id
                ORDER BY common_cnt DESC, l2.user_id
                LIMIT 1
            )
            SELECT DISTINCT f.*, r.name AS rating_name
            FROM films f
            JOIN likes l ON f.id = l.film_id
            JOIN similar_user su ON l.user_id = su.user_id
            LEFT JOIN ratings r ON f.rating_id = r.id
            WHERE f.id NOT IN (SELECT film_id FROM likes WHERE user_id = :user_id)
            """;
}
