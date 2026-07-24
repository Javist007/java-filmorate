package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.db.FilmDBRepository;
import ru.yandex.practicum.filmorate.repository.film.mapper.FilmRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDBRepository.class, FilmRowMapper.class, TestEntity.class})
class FilmDBRepositoryTest {

    private final FilmDBRepository filmRepo;
    private final TestEntity testEntity;

    @BeforeEach
    void clean() {
        testEntity.cleanTables("film_genre", "likes", "films", "users");
    }

    @Test
    void findAll_whenEmpty_returnsEmpty() {
        assertThat(filmRepo.findAll()).isEmpty();
    }

    @Test
    void create_savesFilm_andGeneratesId() {
        Film f = testEntity.film("New", "desc",
                LocalDate.of(2022, 12, 12), 150, 3);

        Film created = filmRepo.create(f);
        assertThat(created.getId()).isPositive();

        Optional<Film> fromDb = filmRepo.findById(created.getId());
        assertThat(fromDb).hasValueSatisfying(db -> {
            assertThat(db.getName()).isEqualTo("New");
            assertThat(db.getMpa().getId()).isEqualTo(3L);
        });
    }

    @Test
    void create_invalidMpa_throwsDataIntegrity() {
        Film f = testEntity.film("Bad", "desc",
                LocalDate.of(2020, 1, 1), 100, 999);

        assertThatThrownBy(() -> filmRepo.create(f))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void update_changesFilm() {
        long id = testEntity.insertFilm("Old", LocalDate.of(2000, 1, 1), 100, 1);

        Film upd = testEntity.film("New", "new desc",
                LocalDate.of(2002, 3, 3), 180, 4);
        upd.setId(id);
        filmRepo.update(upd);

        Optional<Film> after = filmRepo.findById(id);
        assertThat(after).hasValueSatisfying(db -> {
            assertThat(db.getName()).isEqualTo("New");
            assertThat(db.getMpa().getId()).isEqualTo(4L);
        });
    }

    @Test
    void delete_removesFilm() {
        long id = testEntity.insertFilm("Del", LocalDate.of(2010, 10, 10), 80, 1);
        filmRepo.delete(id);

        assertThat(filmRepo.findById(id)).isEmpty();
    }

    @Test
    void getPopular_noLikes_returnsAll() {
        testEntity.insertFilm("A", LocalDate.of(2000, 1, 1), 120, 1);
        testEntity.insertFilm("B", LocalDate.of(2001, 2, 2), 90, 2);

        List<Film> popular = filmRepo.getPopular(10);
        assertThat(popular).hasSize(2);
    }

    @Test
    void getPopular_orderedByLikes() {
        long a = testEntity.insertFilm("A", LocalDate.of(2000, 1, 1), 120, 1);
        long b = testEntity.insertFilm("B", LocalDate.of(2001, 2, 2), 90, 2);

        long u1 = testEntity.insertUser("u1@mail.ru", "u1");
        long u2 = testEntity.insertUser("u2@mail.ru", "u2");
        long u3 = testEntity.insertUser("u3@mail.ru", "u3");

        testEntity.addLikes(b, List.of(u1, u2, u3));
        testEntity.addLikes(a, List.of(u1, u2));

        List<Film> top = filmRepo.getPopular(5);
        assertThat(top).extracting(Film::getId)
                .containsExactly(b, a);
    }
}