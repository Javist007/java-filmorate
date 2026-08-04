package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.repository.events.EventStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;
import ru.yandex.practicum.filmorate.service.mapper.EventMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Бизнес‑слой для событий.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final EventStorage eventStorage;
    private final UserStorage repository;

    public List<EventDto> getFeed(Long userId) {
        log.debug("Запрос ленты событий для пользователя с ID: {}", userId);

        repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));


        List<EventDto> feed = eventStorage.findEventsByUserId(userId).stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());

        log.debug("Возвращена лента из {} событий для пользователя ID: {}", feed.size(), userId);
        return feed;
    }

    public void saveEvent(Long userId, Long entityId, EventType eventType, EventOperation operation) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(entityId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setTimestamp(java.time.Instant.now().toEpochMilli());

        eventStorage.saveEvent(event);
        log.debug("Событие сохранено в БД {}", event);
    }
}