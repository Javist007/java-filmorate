package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.BaseStorage;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class MpaDBRepository extends BaseStorage<Mpa> implements MpaStorage {
    public MpaDBRepository(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Mpa> findAll() {
        log.debug("Возвращаем список всех рейтингов MPA");
        return findMany(MpaSQLRequests.FIND_ALL_MPA);
    }

    @Override
    public Optional<Mpa> findById(long id) {
        log.debug("Возвращаем рейтинг MPA по ID: {}", id);
        return findOne(MpaSQLRequests.FIND_MPA_BY_ID, id);
    }
}
