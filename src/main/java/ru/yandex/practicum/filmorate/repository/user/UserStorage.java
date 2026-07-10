package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    Optional<User> findById(Long id);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    boolean exists(Long id);

    List<User> findAllByIds(Collection<Long> id);

    boolean loginExists(User user);

    boolean emailExists(User user);
}

