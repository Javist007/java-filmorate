package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserResponse;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.friends.FriendStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;
import ru.yandex.practicum.filmorate.service.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Бизнес‑слой для пользователей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage repository;
    private final FriendStorage friendStorage;

    public Collection<UserResponse> findAll() {
        log.info("Получаем список всех пользователей");
        return repository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public UserResponse findById(Long id) {
        return repository.findById(id).map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + id + " не найден"));
    }

    public UserResponse create(@Valid CreateUserRequest request) {
        User user = UserMapper.toEntity(request);
        User newUser = repository.createUser(user);

        log.info("Пользователь '{}' успешно зарегистрирован. ID {}", user.getLogin(), newUser.getId());
        return UserMapper.toDto(newUser);
    }

    public UserResponse update(UpdateUserRequest request) {
        if (repository.findById(request.getId()).isEmpty()) {
            log.warn("Пользователь не найден ID: {}", request.getId());
            throw new NotFoundException("Пользователь с ID: " + request.getId() + " не найден");
        }
        User user = UserMapper.toEntity(request);
        User updateUser = repository.updateUser(user);
        log.debug("Пользователь обновлен ID: {}", updateUser.getId());
        return UserMapper.toDto(updateUser);
    }

    public void delete(long id) {
        findById(id);
        repository.deleteUser(id);
        log.debug("Пользователь удален ID: {}", id);
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            log.error("Попытка пользователя подружиться с самим собой. ID: {}", userId);
            throw new NotFoundException("Пользователь не может подружиться сам с собой");
        }
        findById(userId);
        findById(friendId);

        if (friendStorage.addFriends(userId, friendId)) {
            log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        findById(userId);
        findById(friendId);

        if (friendStorage.deleteFriends(userId, friendId)) {
            log.info("Пользователь {} удалил из друзей пользователем {}", userId, friendId);
        }
    }

    public List<UserResponse> getFriends(Long userId) {
        findById(userId);
        log.info("Запрос списка друзей пользователя ID: {}", userId);
        Set<Long> friendIds = friendStorage.getFriends(userId);
        return repository.findAllByIds(friendIds).stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public List<UserResponse> getCommonFriends(Long userId, Long otherId) {
        findById(userId);
        findById(otherId);
        log.info("Запрос списка общих друзей пользователей ID: {} | {}", userId, otherId);
        Set<Long> commonIds = friendStorage.getCommonFriends(userId, otherId);
        return repository.findAllByIds(commonIds).stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }
}
