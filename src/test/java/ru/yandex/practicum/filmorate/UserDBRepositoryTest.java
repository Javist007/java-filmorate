package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.db.UserDBRepository;
import ru.yandex.practicum.filmorate.repository.user.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDBRepository.class, UserRowMapper.class, TestEntity.class})
class UserDBRepositoryTest {

    private final UserDBRepository repo;
    private final TestEntity testEntity;

    @BeforeEach
    void clean() {
        testEntity.cleanTables("users");
    }

    @Test
    void findAll_whenEmpty_returnsEmpty() {
        assertThat(repo.findAll()).isEmpty();
    }

    @Test
    void create_savesUser_andGeneratesId() {
        User u = testEntity.user("new@example.com", "newbie",
                "Newbie", LocalDate.of(1990, 1, 1));

        User created = repo.createUser(u);
        assertThat(created.getId()).isPositive();

        Optional<User> fromDb = repo.findById(created.getId());
        assertThat(fromDb).hasValueSatisfying(db ->
                assertThat(db.getLogin()).isEqualTo("newbie"));
    }

    @Test
    void update_changesUser() {
        long id = testEntity.insertUser("old@mail.ru", "oldLogin");

        User upd = testEntity.user("updated@example.com", "updatedLogin",
                "Updated", LocalDate.of(2001, 2, 2));
        upd.setId(id);
        repo.updateUser(upd);

        Optional<User> after = repo.findById(id);
        assertThat(after).hasValueSatisfying(db -> {
            assertThat(db.getEmail()).isEqualTo("updated@example.com");
            assertThat(db.getLogin()).isEqualTo("updatedLogin");
            assertThat(db.getBirthday()).isEqualTo(LocalDate.of(2001, 2, 2));
        });
    }

    @Test
    void delete_removesUser() {
        long id = testEntity.insertUser("delete@mail.ru", "toDelete");

        repo.deleteUser(id);
        assertThat(repo.findById(id)).isEmpty();
    }

}

