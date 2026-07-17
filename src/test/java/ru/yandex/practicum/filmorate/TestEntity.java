package ru.yandex.practicum.filmorate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.db.FilmSQLRequests;
import ru.yandex.practicum.filmorate.repository.like.db.LikeSQLRequests;
import ru.yandex.practicum.filmorate.repository.user.db.UserSQLRequests;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
public class TestEntity {

    private final JdbcTemplate jdbc;

    public TestEntity(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Film film(String name,
                     String description,
                     LocalDate releaseDate,
                     int duration,
                     long mpaId) {
        Film f = new Film();
        f.setName(name);
        f.setDescription(description);
        f.setReleaseDate(releaseDate);
        f.setDuration(duration);

        Mpa m = new Mpa();
        m.setId(mpaId);
        f.setMpa(m);

        return f;
    }

    public User user(String email,
                     String login,
                     String name,
                     LocalDate birthday) {
        User u = new User();
        u.setEmail(email);
        u.setLogin(login);
        u.setName(name);
        u.setBirthday(birthday);
        return u;
    }

    public long insertFilm(String name, LocalDate release, int duration, long mpaId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(FilmSQLRequests.INSERT_FILM, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, "test");
            ps.setDate(3, java.sql.Date.valueOf(release));
            ps.setInt(4, duration);
            ps.setLong(5, mpaId);
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public long insertUser(String email, String login) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(UserSQLRequests.INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, login);
            ps.setString(3, login);
            ps.setDate(4, java.sql.Date.valueOf(LocalDate.of(2000, 1, 1)));
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public void addLikes(long filmId,
                         List<Long> userIds) {
        for (Long uid : userIds) {
            jdbc.update(LikeSQLRequests.INSERT_LIKE, filmId, uid);
        }
    }

    public void cleanTables(String... tables) {
        for (String t : tables) {
            jdbc.execute("DELETE FROM " + t);
        }
    }
}

