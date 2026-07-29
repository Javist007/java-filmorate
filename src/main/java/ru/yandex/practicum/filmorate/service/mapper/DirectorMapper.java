package ru.yandex.practicum.filmorate.service.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.director.CreateDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.DirectorResponse;
import ru.yandex.practicum.filmorate.model.Director;

/**
 * Вспомогательный класс для конвертации Director <-> DTO.
 */
@UtilityClass
public class DirectorMapper {

    public Director toEntity(CreateDirectorRequest request) {
        return new Director() {{
            setName(request.getName());
        }};
    }

    public Director toEntity(UpdateDirectorRequest request) {
        return new Director() {{
            setId(request.getId());
            setName(request.getName());
        }};
    }

    public DirectorResponse toDto(Director entity) {
        return new DirectorResponse() {{
            setId(entity.getId());
            setName(entity.getName());
        }};
    }
}
