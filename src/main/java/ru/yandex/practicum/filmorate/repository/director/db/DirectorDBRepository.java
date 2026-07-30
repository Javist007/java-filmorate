package ru.yandex.practicum.filmorate.repository.director.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.BaseStorage;
import ru.yandex.practicum.filmorate.repository.director.DirectorStorage;

import java.util.*;

@Repository
@Slf4j
@Primary
public class DirectorDBRepository extends BaseStorage<Director> implements DirectorStorage {

    public DirectorDBRepository(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Director> findAll() {
        log.debug("Возвращаем список всех режиссёров");
        return findMany(DirectorSQLRequests.FIND_ALL_DIRECTORS);
    }

    @Override
    public Optional<Director> findById(long id) {
        log.debug("Получаем режиссёра по ID: {}", id);
        return findOne(DirectorSQLRequests.FIND_DIRECTOR_BY_ID, id);
    }

    @Override
    public Director createDirector(Director director) {
        long id = insert(DirectorSQLRequests.INSERT_DIRECTOR,
                director.getName());
        director.setId(id);
        log.debug("Создан режиссёр ID {}", id);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        update(DirectorSQLRequests.UPDATE_DIRECTOR,
                director.getName(),
                director.getId());
        log.debug("Обновлен режиссёр ID {}", director.getId());
        return director;
    }

    @Override
    public void deleteDirector(long id) {
        delete(DirectorSQLRequests.DELETE_DIRECTOR, id);
        log.info("Удалён режиссёр ID {}", id);
    }

    @Override
    public void addDirectorsToFilm(long filmId, List<Long> directorIds) {
        log.debug("Добавляем режиссёров к фильму ID: {}", filmId);
        jdbc.batchUpdate(DirectorSQLRequests.INSERT_FILM_DIRECTOR,
                directorIds, directorIds.size(),
                (ps, directorId) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, directorId);
                });
    }

    @Override
    public Set<Long> existsDirectorIds(Set<Long> ids) {
        log.debug("Поиск {} режиссеров по ID", ids.size());
        if (ids.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(namedJdbc.queryForList(
                DirectorSQLRequests.FIND_EXISTING_IDS,
                Map.of("ids", ids), Long.class));
    }

    @Override
    public Map<Long, List<Director>> findDirectorsByFilmIds(Set<Long> filmIds) {
        log.debug("Получаем режиссёров по ID фильмов: {}", filmIds);
        if (filmIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, List<Director>> result = new HashMap<>();
        namedJdbc.query(DirectorSQLRequests.FIND_DIRECTORS_BY_FILM_IDS,
                Map.of("ids", filmIds),
                rs -> {
                    long filmId = rs.getLong("film_id");
                    Director director = mapper.mapRow(rs, rs.getRow());
                    result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(director);
                });
        return result;
    }
}
