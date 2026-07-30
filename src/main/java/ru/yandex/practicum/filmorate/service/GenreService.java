package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.GenreStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Бизнес‑слой для жанров.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> findAll() {
        log.debug("Возвращаем список всех жанров");
        return genreStorage.findAll();
    }

    public Genre findById(long id) {
        log.debug("Получение жанра по ID: {}", id);
        return genreStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с ID: " + id + "не найден"));
    }

    public void updateFilmGenres(long filmId, List<Long> genreIds) {
        if (genreIds != null) {
            log.debug("Обновление жанра фильма - фильм ID: {}, Жанр ID: {}", filmId, genreIds);
            Set<Long> existingIds = genreStorage.findExistGenreId(
                    new HashSet<>(genreIds));
            if (existingIds.size() != genreIds.size()) {
                throw new NotFoundException("Есть отсутствующие жанры");
            }
        }
        genreStorage.updateFilmGenres(filmId, genreIds);
    }

    public Map<Long, List<Genre>> findGenresByFilmIds(Set<Long> filmIds) {
        return genreStorage.findGenresByFilmIds(filmIds);
    }
}
