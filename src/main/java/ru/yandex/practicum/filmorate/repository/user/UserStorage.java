package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    Optional<User> findById(Long id);

    User add(User user);

    void update(User user);

    void delete(Long id);

    boolean loginExists(User user);

    boolean emailExists(User user);
}

