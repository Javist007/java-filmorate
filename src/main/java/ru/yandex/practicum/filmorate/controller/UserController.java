package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserResponse;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

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
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        log.info("GET /users/{} – получение конкретного пользователя", id);
        return userService.findById(id);
    }

    @PostMapping
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        log.info("POST /users – создание пользователя: {}", request.getLogin());
        return userService.create(request);
    }

    @PutMapping
    public UserResponse update(@Valid @RequestBody UpdateUserRequest request) {
        log.info("PUT /users – обновление пользователя ID={}", request.getId());
        return userService.update(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive long id) {
        log.info("DELETE /users - удаление пользователя ID={}", id);
        userService.delete(id);
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
        return userService.getFriends(id);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<UserResponse> getCommonFriends(@PathVariable Long userId,
                                               @PathVariable Long otherId) {
        log.info("GET /users/{}/friends/common/{} – поиск общих друзей", userId, otherId);
        return userService.getCommonFriends(userId, otherId);
    }
}
