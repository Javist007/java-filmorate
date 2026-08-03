package ru.yandex.practicum.filmorate.repository.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {

    List<Genre> findAll();

    Optional<Genre> findById(long id);

    Map<Long, List<Genre>> findGenresByFilmIds(Set<Long> filmIds);

    void addGenresToFilm(long filmId, List<Long> genreIds);

    void deleteGenresFromFilm(long filmId);

    Set<Long> findExistGenreId(Set<Long> id);
}
