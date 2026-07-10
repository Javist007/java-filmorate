package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserResponse;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.friends.FriendStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;
import ru.yandex.practicum.filmorate.service.mapper.UserMapper;
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

    private final UserStorage repository;
    private final FriendStorage friendStorage;

    public Collection<UserResponse> findAll() {
        log.info("Получаем список всех пользователей");
        return repository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserResponse getByIdResponse(Long id) {
        User user = findById(id);
        return UserMapper.toDto(user);
    }

    public UserResponse create(UserRequest request) {
        User user = UserMapper.toEntity(request);
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

        User saved = repository.createUser(user);
        log.info("Пользователь '{}' успешно зарегистрирован. ID {}", user.getLogin(), saved.getId());
        return UserMapper.toDto(saved);
    }

    public UserResponse update(UserRequest request) {
        if (request.getId() == null) {
            log.warn("Отсутствует ID у объекта. Данные: {}", request);
            throw new NotFoundException("ID должен быть указан");
        }
        User existing = findById(request.getId());

        Updater.updateField(log, existing.getId(), USER, "name", request.getName(),
                existing.getName(), existing::setName);

        Updater.updateField(log, existing.getId(), USER, "birthday", request.getBirthday(),
                existing.getBirthday(), existing::setBirthday);

        if (request.getEmail() != null && !existing.getEmail().equals(request.getEmail())) {
            if (repository.emailExists(UserMapper.toEntity(request))) {
                log.warn("Пользователь {} пытается сменить email на уже используемый: {}", existing.getLogin(), request.getEmail());
                throw new DuplicateException(DUPLICATE_EMAIL);
            }
            existing.setEmail(request.getEmail());
            log.info("Пользователь '{}' сменил email на {}", existing.getLogin(), request.getEmail());
        }

        if (request.getLogin() != null && !existing.getLogin().equals(request.getLogin())) {
            if (repository.loginExists(UserMapper.toEntity(request))) {
                log.warn("Пользователь {} пытается сменить login на уже используемый: {}", existing.getLogin(), request.getLogin());
                throw new DuplicateException(DUPLICATE_LOGIN);
            }
            existing.setLogin(request.getLogin());
            log.info("Пользователь '{}' сменил login на {}", existing.getId(), request.getLogin());
        }

        User updated = repository.updateUser(existing);
        return UserMapper.toDto(updated);
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            log.error("Попытка пользователя подружиться с самим собой. ID: {}", userId);
            throw new NotFoundException("Пользователь не может подружиться сам с собой");
        }
        findById(userId);
        findById(friendId);

        boolean addedToUser = friendStorage.addFriends(userId, friendId);
        boolean addedToFriend = friendStorage.addFriends(friendId, userId);

        if (addedToUser || addedToFriend) {
            log.info("Пользователь {} теперь друг пользователя {}", userId, friendId);
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        findById(userId);
        findById(friendId);

        boolean removedFromUser = friendStorage.deleteFriends(userId, friendId);
        boolean removedFromFriend = friendStorage.deleteFriends(friendId, userId);

        if (removedFromUser || removedFromFriend) {
            log.info("Пользователь {} разорвал дружбу с пользователем {}", userId, friendId);
        }
    }

    public List<UserResponse> getFriends(Long userId) {
        findById(userId);
        Set<Long> friendIds = friendStorage.getFriends(userId);
        return repository.findAllByIds(friendIds).stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getCommonFriends(Long userId, Long otherId) {
        findById(userId);
        findById(otherId);
        Set<Long> commonIds = friendStorage.getCommonFriends(userId, otherId);
        return repository.findAllByIds(commonIds).stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь не найден. ID: {}", id);
                    return new NotFoundException("Пользователь ID: = " + id + " не найден");
                });
    }
}
