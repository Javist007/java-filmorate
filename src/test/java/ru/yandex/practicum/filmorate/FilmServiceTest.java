package ru.yandex.practicum.filmorate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;
import ru.yandex.practicum.filmorate.service.FilmService;

class FilmServiceTest {

    private FilmService filmService;
    private UserRepository userRepo;

    @BeforeEach
    void setUp() {
        FilmRepository filmRepo = new FilmRepository();
        userRepo = new UserRepository();
        filmService = new FilmService(filmRepo, userRepo);
    }

    @Test
    void create_ShouldAssignIdAndReturnFilm() {
        Film f = new Film();
        f.setName("Matrix");
        f.setReleaseDate(LocalDate.of(1999, 3, 31));
        f.setDuration(136);

        Film created = filmService.create(f);
        assertThat(created.getId()).isNotNull();
        assertEquals("Matrix", created.getName());
    }

    @Test
    void update_ShouldModifyOnlyNonNullFields() {
        Film f1 = new Film();
        f1.setName("Old");
        f1.setDuration(100);
        Film stored = filmService.create(f1);

        Film upd = new Film();
        upd.setId(stored.getId());
        upd.setName("New");

        Film updated = filmService.update(upd);
        assertEquals("New", updated.getName());
        assertEquals(100, updated.getDuration());
    }

    @Test
    void addLike_ShouldAddAndDuplicateThrows() {
        User user = new User();
        user.setLogin("user1");
        userRepo.add(user);
        Film film = getFilm("Film1", 90);

        filmService.addLike(film.getId(), user.getId());
        assertTrue(film.getLikedBy().contains(user.getId()));

        assertThrows(DuplicateException.class,
                () -> filmService.addLike(film.getId(), user.getId()));
    }

    @Test
    void removeLike_ShouldRemove() {
        var user = new ru.yandex.practicum.filmorate.model.User();
        user.setLogin("user2");
        userRepo.add(user);
        Film film = getFilm("Film1", 90);

        filmService.addLike(film.getId(), user.getId());
        assertTrue(film.getLikedBy().contains(user.getId()));

        filmService.removeLike(film.getId(), user.getId());
        assertFalse(film.getLikedBy().contains(user.getId()));
    }


    @Test
    void getPopular_ShouldReturnSortedList() {
        User u1 = new User();
        u1.setLogin("a");
        userRepo.add(u1);

        User u2 = new User();
        u2.setLogin("b");
        userRepo.add(u2);

        Film f1 = getFilm("Film1", 100);
        filmService.create(f1);

        Film f2 = getFilm("Film2", 120);
        filmService.create(f2);

        filmService.addLike(f1.getId(), u1.getId());
        filmService.addLike(f1.getId(), u2.getId());
        filmService.addLike(f2.getId(), u1.getId());

        List<Film> popular = filmService.getPopular(2);
        assertEquals(List.of(f1, f2), popular);
    }

    private Film getFilm(String name, Integer duration) {
        Film f = new Film();
        f.setName(name);
        f.setReleaseDate(LocalDate.now());
        f.setDuration(duration);
        return filmService.create(f);
    }
}

