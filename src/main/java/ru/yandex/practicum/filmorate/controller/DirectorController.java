package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid; // 1. Добавили обязательный импорт
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.director.CreateDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.DirectorResponse;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

/**
 * REST‑контроллер для режиссёров.
 */
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public List<DirectorResponse> getAllDirectors() {
        log.info("GET /directors – получение списка режиссёров");
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public DirectorResponse getDirector(@PathVariable long id) {
        log.info("GET /directors/{} – получение конкретного режиссёра", id);
        return directorService.findById(id);
    }

    @PostMapping
    public DirectorResponse create(@Valid @RequestBody CreateDirectorRequest request) { // 2. Добавили @Valid
        log.info("POST /directors – создание режиссёра: {}", request.getName());
        return directorService.create(request);
    }

    @PutMapping
    public DirectorResponse update(@Valid @RequestBody UpdateDirectorRequest request) { // 3. Добавили @Valid
        log.info("PUT /directors – обновление режиссёра ID={}", request.getId());
        return directorService.update(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("DELETE /directors/{} – удаление режиссёра", id);
        directorService.delete(id);
    }
}
