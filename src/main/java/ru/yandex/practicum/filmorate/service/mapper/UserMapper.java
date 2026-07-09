package ru.yandex.practicum.filmorate.service.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.user.UserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserResponse;
import ru.yandex.practicum.filmorate.model.User;

/**
 * Вспомогательный класс для конвертации User -> DTO.
 */
@UtilityClass
public class UserMapper {

    public User toEntity(UserRequest dto) {
        return new User() {{
            setId(dto.getId());
            setEmail(dto.getEmail());
            setLogin(dto.getLogin());
            setName(dto.getName());
            setBirthday(dto.getBirthday());
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
