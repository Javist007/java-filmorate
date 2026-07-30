package ru.yandex.practicum.filmorate.service.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

/**
 * Вспомогательный класс для конвертации Film -> DTO.
 */
@UtilityClass
public class FilmMapper {

    public Film toEntity(CreateFilmRequest request) {
        return new Film() {{
            setName(request.getName());
            setDescription(request.getDescription());
            setReleaseDate(request.getReleaseDate());
            setDuration(request.getDuration());
            if (request.getMpa() != null) {
                Mpa mpa = new Mpa();
                mpa.setId(request.getMpa().getId());
                setMpa(mpa);
            }
        }};
    }

    public Film toEntity(UpdateFilmRequest request) {
        return new Film() {{
            setId(request.getId());
            setName(request.getName());
            setDescription(request.getDescription());
            setReleaseDate(request.getReleaseDate());
            setDuration(request.getDuration());
            if (request.getMpa() != null) {
                Mpa mpa = new Mpa();
                mpa.setId(request.getMpa().getId());
                setMpa(mpa);
            }
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

