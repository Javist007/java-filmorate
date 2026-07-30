package ru.yandex.practicum.filmorate.repository.film.db;

import lombok.NoArgsConstructor;

/**
 * Основные SQL запросы к фильмам.
 */
@NoArgsConstructor
public class FilmSQLRequests {

    public static final String FIND_ALL_FILMS = """
            SELECT f.*, r.name AS rating_name
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            """;

    public static final String FIND_FILM_BY_ID = """
            SELECT f.*, r.name AS rating_name
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            WHERE f.id = ?
            """;

    public static final String INSERT_FILM = """
            INSERT INTO films(name, description, release_date, duration, rating_id)
            VALUES(?, ?, ?, ?, ?)
            """;

    public static final String UPDATE_FILM = """
            UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?
            """;

    public static final String DELETE_FILM = "DELETE FROM films WHERE id = ?";

    public static final String FIND_POPULAR_FILMS = """
            SELECT f.*, r.name AS rating_name
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE f.id IN (
                        SELECT DISTINCT f2.id
                        FROM films f2
                        LEFT JOIN film_genre fg ON f2.id = fg.film_id
                        WHERE (? IS NULL OR fg.genre_id = ?)
            )
            AND (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?)
            GROUP BY f.id, r.name
            ORDER BY COUNT(l.user_id) DESC LIMIT ?
            """;

    public static final String FIND_COMMON_FILMS = """
            SELECT f.*, r.name AS rating_name
            FROM films f
            JOIN ratings r ON f.rating_id = r.id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE f.id IN (
                SELECT l1.film_id
                FROM likes l1
                WHERE l1.user_id = ?
            )
            AND f.id IN (
                SELECT l2.film_id
                FROM likes l2
                WHERE l2.user_id = ?
            )
            GROUP BY f.id, r.name
            ORDER BY COUNT(l.user_id) DESC
            """;

    public static final String FIND_FILMS_LIKES_SORT = """
            SELECT f.*, r.name AS rating_name
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE f.id IN ( SELECT film_id
                FROM film_director
                WHERE director_id = ?
            )
            GROUP BY f.id ORDER BY COUNT(l.user_id) DESC
            """;

    public static final String FIND_FILMS_YEAR_SORT = """
            SELECT f.*, r.name AS rating_name
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            WHERE f.id IN ( SELECT film_id
                FROM film_director
                WHERE director_id = ?
            )
            ORDER BY EXTRACT(YEAR FROM f.release_date)
            """;

    public static final String SEARCH_TITLE = """
            SELECT f.*, r.name AS rating_name
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%'))
            ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC
            """;

    public static final String SEARCH_DIRECTOR = """
            SELECT f.*, r.name AS rating_name
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            WHERE EXISTS (
                SELECT 1 FROM film_director df
                JOIN directors d ON df.director_id = d.id
                WHERE df.film_id = f.id
                  AND LOWER(d.name) LIKE LOWER(CONCAT('%', ?, '%'))
            )
            ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC
            """;

    public static final String SEARCH_BOTH = """
            SELECT f.*, r.name AS rating_name
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%'))
               OR EXISTS (
                   SELECT 1 FROM film_director df
                   JOIN directors d ON df.director_id = d.id
                   WHERE df.film_id = f.id
                     AND LOWER(d.name) LIKE LOWER(CONCAT('%', ?, '%'))
               )
            ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC
            """;
}
