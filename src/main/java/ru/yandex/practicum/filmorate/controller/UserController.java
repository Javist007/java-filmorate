package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.UserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserResponse;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.mapper.UserMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST‑контроллер для пользователей.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserResponse> findAll() {
        log.info("GET /users – получение списка всех пользователей");
        return userService.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        log.info("POST /users – создание пользователя: {}", request.getLogin());
        User created = userService.create(UserMapper.toEntity(request));
        return UserMapper.toDto(created);
    }

    @PutMapping
    public UserResponse update(@Valid @RequestBody UserRequest request) {
        log.info("PUT /users – обновление пользователя ID={}", request.getId());
        User updated = userService.update(UserMapper.toEntity(request));
        return UserMapper.toDto(updated);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        log.info("GET /users/{} – получение конкретного пользователя", id);
        User u = userService.findById(id);
        return UserMapper.toDto(u);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("PUT /users/{}/friends/{} – пользователь {} добавляет друга {}", userId, friendId, userId, friendId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("DELETE /users/{}/friends/{} – пользователь {} удаляет друга {}", userId, friendId, userId, friendId);
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserResponse> getFriends(@PathVariable Long id) {
        log.info("GET /users/{}/friends – получение списка друзей пользователя", id);
        return userService.getFriends(id).stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<UserResponse> getCommonFriends(@PathVariable Long userId,
                                               @PathVariable Long otherId) {
        log.info("GET /users/{}/friends/common/{} – поиск общих друзей", userId, otherId);
        return userService.getCommonFriends(userId, otherId).stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }
}
