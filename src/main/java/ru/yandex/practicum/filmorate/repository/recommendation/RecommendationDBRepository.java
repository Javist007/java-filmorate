package ru.yandex.practicum.filmorate.repository.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.BaseStorage;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class RecommendationDBRepository extends BaseStorage<Film> implements RecommendationStorage {

    public RecommendationDBRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findRecommendedFilms(Long userId) {
        log.debug("Нахождение рекомендаций для пользователя ID: {}", userId);
        return namedJdbc.query(RecommendationSQLRequests.FIND_RECOMMEND_FILM, Map.of("user_id", userId), mapper);
    }
}
