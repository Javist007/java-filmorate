package ru.yandex.practicum.filmorate.repository.user.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.BaseStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@Primary
public class UserDBRepository extends BaseStorage<User> implements UserStorage {

    NamedParameterJdbcTemplate namedJdbc;

    public UserDBRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
        this.namedJdbc = new NamedParameterJdbcTemplate(jdbc);
    }

    @Override
    public Collection<User> findAll() {
        log.debug("Возвращаем список всех пользователей");
        return findMany(UserSQLRequests.FIND_ALL_USERS);
    }

    @Override
    public Optional<User> findById(Long id) {
        log.debug("Возвращаем пользователя по ID: {}", id);
        return findOne(UserSQLRequests.FIND_USER_BY_ID, id);
    }

    @Override
    public User createUser(User user) {
        long id = insert(UserSQLRequests.INSERT_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        user.setId(id);
        log.debug("Пользователь создан ID: {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        update(UserSQLRequests.UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        log.debug("Пользователь обновлен ID: {}", user.getId());
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        delete(UserSQLRequests.DELETE_USER, id);
        log.debug("Пользователь удален ID: {}", id);
    }

    @Override
    public List<User> findAllByIds(Collection<Long> id) {
        if (id == null || id.isEmpty()) {
            log.debug("Список пуст...");
            return List.of();
        }
        MapSqlParameterSource params = new MapSqlParameterSource("ids", id);
        List<User> users = namedJdbc.query(UserSQLRequests.FIND_USERS_BY_IDS, params, mapper);
        log.debug("Пользователей: {} - найдено по ID", users.size());
        return users;
    }
}
