package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;
import ru.yandex.practicum.filmorate.service.util.Updater;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Бизнес‑слой для пользователей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    public static final String DUPLICATE_EMAIL = "Данная электронная почта уже используется";
    public static final String DUPLICATE_LOGIN = "Данный логин уже используется";
    public static final String USER = "Пользователь";

    private final UserRepository repository;

    public Collection<User> findAll() {
        log.info("Получаем список всех пользователей");
        return repository.findAll();
    }

    public User create(User user) {
        if (repository.loginExists(user)) {
            log.warn("Регистрация с уже используемым логином: {}", user.getLogin());
            throw new DuplicateException(DUPLICATE_LOGIN);
        }
        if (repository.emailExists(user)) {
            log.warn("Регистрация с уже используемой почтой: {}", user.getEmail());
            throw new DuplicateException(DUPLICATE_EMAIL);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        User saved = repository.add(user);
        log.info("Пользователь '{}' успешно зарегистрирован. ID {}", user.getLogin(), saved.getId());
        return saved;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.warn("Отсутствует ID у объекта. Данные: {}", newUser);
            throw new NotFoundException("ID должен быть указан");
        }
        User existing = getById(newUser.getId());

        Updater.updateField(log, existing.getId(), USER, "name", newUser.getName(),
                existing.getName(), existing::setName);

        Updater.updateField(log, existing.getId(), USER, "birthday", newUser.getBirthday(),
                existing.getBirthday(), existing::setBirthday);

        if (newUser.getEmail() != null && !existing.getEmail().equals(newUser.getEmail())) {
            if (repository.emailExists(newUser)) {
                log.warn("Пользователь {} пытается сменить email на уже используемый: {}", existing.getLogin(), newUser.getEmail());
                throw new DuplicateException(DUPLICATE_EMAIL);
            }
            existing.setEmail(newUser.getEmail());
            log.info("Пользователь '{}' сменил email на {}", existing.getLogin(), newUser.getEmail());
        }

        if (newUser.getLogin() != null && !existing.getLogin().equals(newUser.getLogin())) {
            if (repository.loginExists(newUser)) {
                log.warn("Пользователь {} пытается сменить login на уже используемый: {}", existing.getLogin(), newUser.getLogin());
                throw new DuplicateException(DUPLICATE_LOGIN);
            }
            existing.setLogin(newUser.getLogin());
            log.info("Пользователь '{}' сменил login на {}", existing.getId(), newUser.getLogin());
        }

        return repository.add(existing);
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            log.error("Попытка пользователя подружиться с самим собой. ID: {}", userId);
            throw new NotFoundException("Пользователь не может подружиться сам с собой");
        }
        User user = getById(userId);
        User friend = getById(friendId);

        boolean addedToUser   = user.getFriends().add(friendId);
        boolean addedToFriend = friend.getFriends().add(userId);

        if (addedToUser || addedToFriend) {
            log.info("Пользователь {} теперь друг пользователя {}", userId, friendId);
            repository.update(user);
            repository.update(friend);
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);

        boolean removedFromUser   = user.getFriends().remove(friendId);
        boolean removedFromFriend = friend.getFriends().remove(userId);

        if (removedFromUser || removedFromFriend) {
            log.info("Пользователь {} разорвал дружбу с пользователем {}", userId, friendId);
            repository.update(user);
            repository.update(friend);
        }
    }

    public List<User> getFriends(Long userId) {
        User user = getById(userId);
        return user.getFriends().stream()
                .map(id -> repository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Друг не найден: ID=" + id)))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User u1 = getById(userId);
        User u2 = getById(otherId);

        Set<Long> commonIds = new HashSet<>(u1.getFriends());
        commonIds.retainAll(u2.getFriends());

        return commonIds.stream()
                .map(id -> repository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Друг не найден: ID=" + id)))
                .collect(Collectors.toList());
    }

    public User getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь не найден. ID: {}", id);
                    return new NotFoundException(
                            "Пользователь ID: = " + id + " не найден");
                });
    }
}
