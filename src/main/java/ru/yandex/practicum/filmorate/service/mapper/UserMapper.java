package ru.yandex.practicum.filmorate.service.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.user.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserResponse;
import ru.yandex.practicum.filmorate.model.User;

/**
 * Вспомогательный класс для конвертации User -> DTO.
 */
@UtilityClass
public class UserMapper {

    public User toEntity(UpdateUserRequest request) {
        if (request == null) return null;

        User user = new User();
        user.setId(request.getId());
        fillCommonFields(user, request.getEmail(), request.getLogin(), request.getName(), request.getBirthday());
        return user;
    }

    public User toEntity(CreateUserRequest request) {
        if (request == null) return null;

        User user = new User();
        fillCommonFields(user, request.getEmail(), request.getLogin(), request.getName(), request.getBirthday());
        return user;
    }

    public UserResponse toDto(User entity) {
        if (entity == null) return null;

        UserResponse dto = new UserResponse();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setLogin(entity.getLogin());
        dto.setName(entity.getName());
        dto.setBirthday(entity.getBirthday());
        return dto;
    }

    private void fillCommonFields(User user, String email, String login, String name, java.time.LocalDate birthday) {
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name == null || name.isBlank() ? login : name);
        user.setBirthday(birthday);
    }
}