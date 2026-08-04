package ru.yandex.practicum.filmorate.repository.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {

    List<Director> findAll();

    Optional<Director> findById(long id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(long id);

    void addDirectorsToFilm(long filmId, List<Long> directorIds);

    Set<Long> existsDirectorIds(Set<Long> ids);

    Map<Long, List<Director>> findDirectorsByFilmIds(Set<Long> filmIds);

    void deleteDirectorsFromFilm(long filmId);
}
