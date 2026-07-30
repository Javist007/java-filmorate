package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.director.db.DirectorDBRepository;
import ru.yandex.practicum.filmorate.repository.director.mapper.DirectorRowMapper;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        DirectorDBRepository.class,
        DirectorRowMapper.class,
        TestEntity.class
})
class DirectorDBRepositoryTest {

    private final DirectorDBRepository directorRepo;
    private final TestEntity testEntity;

    @BeforeEach
    void clean() {
        testEntity.cleanTables("film_director", "directors");
    }

    @Test
    void findAll_whenEmpty_returnsEmpty() {
        assertThat(directorRepo.findAll()).isEmpty();
    }

    @Test
    void create_savesDirector_andGeneratesId() {
        Director d = new Director();
        d.setName("Christopher Nolan");

        Director created = directorRepo.createDirector(d);
        assertThat(created.getId()).isPositive();

        Optional<Director> fromDb = directorRepo.findById(created.getId());
        assertThat(fromDb).hasValueSatisfying(db ->
                assertThat(db.getName()).isEqualTo("Christopher Nolan"));
    }

    @Test
    void update_changesDirector() {
        Director d = new Director();
        d.setName("Old Name");
        Director created = directorRepo.createDirector(d);

        created.setName("New Name");
        directorRepo.updateDirector(created);

        Optional<Director> after = directorRepo.findById(created.getId());
        assertThat(after).hasValueSatisfying(db ->
                assertThat(db.getName()).isEqualTo("New Name"));
    }

    @Test
    void delete_removesDirector() {
        Director d = new Director();
        d.setName("To be deleted");
        Director created = directorRepo.createDirector(d);

        directorRepo.deleteDirector(created.getId());

        assertThat(directorRepo.findById(created.getId())).isEmpty();
    }

    @Test
    void existsDirectorIds_returnsExistingSet() {
        Director d1 = new Director();
        d1.setName("A");
        Director d2 = new Director();
        d2.setName("B");

        long id1 = directorRepo.createDirector(d1).getId();
        long id2 = directorRepo.createDirector(d2).getId();

        Set<Long> idsToCheck = Set.of(id1, id2);
        Set<Long> existing = directorRepo.existsDirectorIds(idsToCheck);

        assertThat(existing).containsExactlyInAnyOrderElementsOf(idsToCheck);
    }

    @Test
    void findDirectorsByFilmIds_returnsMapWithDirectors() {
        long filmId = testEntity.insertFilm(
                "Interstellar",
                LocalDate.of(2014, 11, 7),
                169,
                1);

        Director director = new Director();
        director.setName("Christopher Nolan");
        long dirId = directorRepo.createDirector(director).getId();

        directorRepo.addDirectorsToFilm(filmId, List.of(dirId));

        Set<Long> filmIds = Set.of(filmId);
        Map<Long, List<Director>> result = directorRepo.findDirectorsByFilmIds(filmIds);

        assertThat(result).hasSize(1);
        assertThat(result.get(filmId)).hasSize(1)
                .first()
                .satisfies(d -> {
                    assertThat(d.getName()).isEqualTo("Christopher Nolan");
                    assertThat(d.getId()).isEqualTo(dirId);
                });
    }
}
