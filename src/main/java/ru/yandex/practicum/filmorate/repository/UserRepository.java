package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Репозиторий для пользователей.
 */
@Repository
public class UserRepository {

    private final Map<Long, User> storage = new LinkedHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public Collection<User> findAll() {
        return List.copyOf(storage.values());
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.incrementAndGet());
        }
        storage.put(user.getId(), user);
        return user;
    }

    public void delete(Long id) {
        storage.remove(id);
    }

    public boolean loginExists(String login) {
        return storage.values().stream()
                .anyMatch(u -> u.getLogin().equals(login));
    }

    public boolean emailExists(String email) {
        return storage.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }
}

