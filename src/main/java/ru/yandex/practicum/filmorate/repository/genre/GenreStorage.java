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

    default void updateFilmGenres(long filmId, List<Long> newGenreIds) {
        if (newGenreIds != null && !newGenreIds.isEmpty()) {
            deleteGenresFromFilm(filmId);
            addGenresToFilm(filmId, newGenreIds);
        }
    }

    Set<Long> findExistGenreId(Set<Long> id);
}
