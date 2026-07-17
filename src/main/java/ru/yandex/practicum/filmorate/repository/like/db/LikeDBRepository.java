package ru.yandex.practicum.filmorate.repository.like.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.repository.BaseStorage;
import ru.yandex.practicum.filmorate.repository.like.LikeStorage;

import java.sql.PreparedStatement;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Slf4j
@Primary
public class LikeDBRepository extends BaseStorage<Like> implements LikeStorage {

    public LikeDBRepository(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Set<Long> getUserIds(Long filmId) {
        log.debug("Возвращаем список лайков к фильму ID: {}", filmId);
        return jdbc.query(LikeSQLRequests.FIND_LIKES_BY_FILM_ID,
                        (resultSet, rowNumber) ->
                                resultSet.getLong("user_id"), filmId)
                .stream()
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        int affectedRows = jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(LikeSQLRequests.INSERT_LIKE);
            ps.setObject(1, filmId);
            ps.setObject(2, userId);
            return ps;
        });
        log.debug("Пользователь ID: {} - поставил лайк фильму ID: {}", userId, filmId);
        return affectedRows > 0;
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        log.debug("Пользователь ID: {} - удалил лайк с фильма ID: {}", userId, filmId);
        return delete(LikeSQLRequests.DELETE_LIKE, filmId, userId);
    }
}
