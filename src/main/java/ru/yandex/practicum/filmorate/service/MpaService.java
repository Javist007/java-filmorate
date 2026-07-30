package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mpa.MpaStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> findAll() {
        log.debug("Получение списка рейтингов MPA");
        return mpaStorage.findAll();
    }

    public Mpa findById(long id) {
        log.debug("Получение рейтинга MPA по ID: {}", id);
        return mpaStorage.findById(id).orElseThrow(() -> new NotFoundException("Рейтинг с ID: " + id + " не найден"));
    }
}
