package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.CreateDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.DirectorResponse;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.director.DirectorStorage;
import ru.yandex.practicum.filmorate.service.mapper.DirectorMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Бизнес‑слой для режиссёров.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public List<DirectorResponse> findAll() {
        log.debug("Получаем список всех режиссёров");
        return directorStorage.findAll().stream()
                .map(DirectorMapper::toDto)
                .toList();
    }

    public DirectorResponse findById(long id) {
        log.debug("Получение режиссёра по ID: {}", id);
        return directorStorage.findById(id)
                .map(DirectorMapper::toDto)
                .orElseThrow(() ->
                        new NotFoundException("Режиссёр с ID: " + id + " не найден"));
    }

    public DirectorResponse create(CreateDirectorRequest request) {
        log.debug("Создаём режиссёра по имени: {}", request.getName());
        Director director = DirectorMapper.toEntity(request);
        return DirectorMapper.toDto(directorStorage.createDirector(director));
    }

    public DirectorResponse update(UpdateDirectorRequest request) {
        isExists(request.getId());
        log.debug("Обновляем режиссёра ID {}", request.getId());
        Director director = DirectorMapper.toEntity(request);
        return DirectorMapper.toDto(directorStorage.updateDirector(director));
    }

    public void delete(long id) {
        isExists(id);
        directorStorage.deleteDirector(id);
        log.info("Удалён режиссёр ID {}", id);
    }

    public void updateFilmDirectors(long filmId, List<Long> directorIds) {
        directorStorage.deleteDirectorsFromFilm(filmId);

        if (directorIds != null && !directorIds.isEmpty()) {
            log.debug("Обновление режиссёров: Фильм ID:{}, Режиссёры ID:{}", filmId, directorIds);
            Set<Long> existingIds = directorStorage.existsDirectorIds(
                    new HashSet<>(directorIds));
            if (existingIds.size() != directorIds.size()) {
                throw new NotFoundException("Один или несколько режиссёров не найдены");
            }
            directorStorage.addDirectorsToFilm(filmId, directorIds);
        } else {
            log.debug("Все режиссёры успешно удалены для фильма ID: {}", filmId);
        }
    }

    public Map<Long, List<Director>> findDirectorsByFilmIds(Set<Long> filmIds) {
        log.debug("Получение списка режиссеров по фильмам: {}", filmIds);
        return directorStorage.findDirectorsByFilmIds(filmIds);
    }

    public void isExists(long id) {
        if (directorStorage.findById(id).isEmpty()) {
            log.warn("Режиссер не найден ID: {}", id);
            throw new NotFoundException("Режиссер с ID: " + id + " не найден");
        }
    }
}