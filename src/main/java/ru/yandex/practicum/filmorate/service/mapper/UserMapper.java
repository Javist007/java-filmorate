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
        return new User() {{
            setId(request.getId());
            setEmail(request.getEmail());
            setLogin(request.getLogin());
            setName(request.getName());
            setBirthday(request.getBirthday());
        }};
    }
    public User toEntity(CreateUserRequest request) {
        return new User() {{
            setEmail(request.getEmail());
            setLogin(request.getLogin());
            setName(request.getName());
            setBirthday(request.getBirthday());
        }};
    }

    public UserResponse toDto(User entity) {
        return new UserResponse() {{
            setId(entity.getId());
            setEmail(entity.getEmail());
            setLogin(entity.getLogin());
            setName(entity.getName());
            setBirthday(entity.getBirthday());
        }};
    }
}
