package ru.yandex.practicum.filmorate.repository.director.db;

import lombok.NoArgsConstructor;

/**
 * Основные SQL запросы к таблице directors и связям с фильмами.
 */
@NoArgsConstructor
public class DirectorSQLRequests {

    public static final String FIND_ALL_DIRECTORS = """
            SELECT * FROM directors
            """;

    public static final String FIND_DIRECTOR_BY_ID = """
            SELECT * FROM directors WHERE id = ?
            """;

    public static final String INSERT_DIRECTOR = """
            INSERT INTO directors(name) VALUES(?)
            """;

    public static final String UPDATE_DIRECTOR = """
            UPDATE directors SET name = ? WHERE id = ?
            """;

    public static final String DELETE_DIRECTOR = """
            DELETE FROM directors WHERE id = ?
            """;

    public static final String INSERT_FILM_DIRECTOR = """
            INSERT INTO film_director (film_id, director_id) VALUES (?, ?)
            """;

    public static final String FIND_EXISTING_IDS = """
            SELECT id FROM directors WHERE id IN (:ids)
            """;

    public static final String FIND_DIRECTORS_BY_FILM_IDS = """
            SELECT fd.film_id, d.id, d.name
            FROM directors d
            JOIN film_director AS fd ON d.id = fd.director_id
            WHERE fd.film_id IN (:ids)
            ORDER BY fd.film_id, d.id
            """;
}
