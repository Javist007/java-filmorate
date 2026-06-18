package ru.yandex.practicum.filmorate.repository.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Репозиторий для пользователей.
 */
@Repository
@Getter
@Slf4j
public class UserRepository implements UserStorage {

    private final Map<Long, User> storage = new LinkedHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public Collection<User> findAll() {
        log.debug("Возвращаем {} пользователей из репозитория", storage.size());
        return List.copyOf(storage.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public User add(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.incrementAndGet());
        }
        storage.put(user.getId(), user);
        log.debug("Пользователь '{}' зарегистрирован с ID {}", user.getLogin(), user.getId());
        return user;
    }

    @Override
    public void update(User user) {
        storage.put(user.getId(), user);
        log.debug("Пользователь ID {} обновлён", user.getId());
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
        log.info("Удалён пользователь с ID {}", id);
    }

    @Override
    public boolean loginExists(User user) {
        return storage.values().stream()
                .anyMatch(u -> u.getLogin().equals(user.getLogin()));
    }

    @Override
    public boolean emailExists(User user) {
        return storage.values().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()));
    }
}
