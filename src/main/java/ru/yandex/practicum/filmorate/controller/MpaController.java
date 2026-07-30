package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

/**
 * REST‑контроллер для рейтингов.
 */
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> findAllMpa() {
        log.info("GET /mpa – получение списка рейтингов");
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable long id) {
        log.info("GET /mpa/{} – получение конкретного рейтинга ", id);
        return mpaService.findById(id);
    }
}
