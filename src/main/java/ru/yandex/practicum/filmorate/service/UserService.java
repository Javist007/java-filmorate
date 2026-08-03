package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserResponse;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;
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
    private final FeedService feedService;

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
        isExists(request.getId());
        User user = UserMapper.toEntity(request);
        User updateUser = repository.updateUser(user);
        log.debug("Пользователь обновлен ID: {}", updateUser.getId());
        return UserMapper.toDto(updateUser);
    }

    public void delete(long id) {
        isExists(id);
        repository.deleteUser(id);
        log.debug("Пользователь удален ID: {}", id);
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            log.error("Попытка пользователя подружиться с самим собой. ID: {}", userId);
            throw new NotFoundException("Пользователь не может подружиться сам с собой");
        }
        isExists(userId);
        isExists(friendId);

        if (friendStorage.addFriends(userId, friendId)) {
            log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);

            feedService.saveEvent(userId, friendId, EventType.FRIEND, EventOperation.ADD);
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        isExists(userId);
        isExists(friendId);

        if (friendStorage.deleteFriends(userId, friendId)) {
            log.info("Пользователь {} удалил из друзей пользователем {}", userId, friendId);

            feedService.saveEvent(userId, friendId, EventType.FRIEND, EventOperation.REMOVE);
        }
    }

    public List<UserResponse> getFriends(Long userId) {
        isExists(userId);
        log.info("Запрос списка друзей пользователя ID: {}", userId);
        Set<Long> friendIds = friendStorage.getFriends(userId);
        return repository.findAllByIds(friendIds).stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public List<UserResponse> getCommonFriends(Long userId, Long otherId) {
        isExists(userId);
        isExists(otherId);
        log.info("Запрос списка общих друзей пользователей ID: {} | {}", userId, otherId);
        Set<Long> commonIds = friendStorage.getCommonFriends(userId, otherId);
        return repository.findAllByIds(commonIds).stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public void isExists(long id) {
        if (repository.findById(id).isEmpty()) {
            log.warn("Пользователь не найден ID: {}", id);
            throw new NotFoundException("Пользователь с ID: " + id + " не найден");
        }
    }
}
