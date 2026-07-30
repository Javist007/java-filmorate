package ru.yandex.practicum.filmorate.repository.review;

public class ReviewSQLRequests {
    private ReviewSQLRequests() {

    }

    public static final String REVIEW_SELECT = """
            SELECT r.review_id,
                   r.content,
                   r.is_positive,
                   r.user_id,
                   r.film_id,
                   COALESCE(SUM(CASE
                       WHEN rr.user_id IS NULL THEN 0
                       WHEN rr.is_useful THEN 1
                       ELSE -1
                   END), 0) AS useful
            FROM reviews r
            LEFT JOIN review_reactions rr ON rr.review_id = r.review_id
            """;

    public static final String FIND_BY_ID = REVIEW_SELECT + """
            WHERE r.review_id = ?
            GROUP BY r.review_id, r.content, r.is_positive, r.user_id, r.film_id
            """;

    public static final String FIND_ALL = REVIEW_SELECT + """
            GROUP BY r.review_id, r.content, r.is_positive, r.user_id, r.film_id
            ORDER BY useful DESC, r.review_id
            LIMIT ?
            """;

    public static final String FIND_BY_FILM_ID = REVIEW_SELECT + """
            WHERE r.film_id = ?
            GROUP BY r.review_id, r.content, r.is_positive, r.user_id, r.film_id
            ORDER BY useful DESC, r.review_id
            LIMIT ?
            """;

    public static final String INSERT = """
            INSERT INTO reviews (content, is_positive, user_id, film_id)
            VALUES (?, ?, ?, ?)
            """;

    public static final String UPDATE = """
            UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?
            """;

    public static final String DELETE = "DELETE FROM reviews WHERE review_id = ?";
}
