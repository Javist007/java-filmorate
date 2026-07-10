package ru.yandex.practicum.filmorate.service.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.film.FilmRequest;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.model.Film;

/**
 * Вспомогательный класс для конвертации Film -> DTO.
 */
@UtilityClass
public class FilmMapper {

    public Film toEntity(FilmRequest dto) {
        return new Film() {{
            setId(dto.getId());
            setName(dto.getName());
            setDescription(dto.getDescription());
            setReleaseDate(dto.getReleaseDate());
            setDuration(dto.getDuration());
        }};
    }

    public FilmResponse toDto(Film entity) {
        return new FilmResponse() {{
            setId(entity.getId());
            setName(entity.getName());
            setDescription(entity.getDescription());
            setReleaseDate(entity.getReleaseDate());
            setDuration(entity.getDuration());
        }};
    }
}

