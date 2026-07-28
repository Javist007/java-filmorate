package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Operation;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.repository.events.EventStorage;
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
    private final UserService userService;

    public List<EventDto> getFeed(Long userId) {
        log.debug("Запрос ленты событий для пользователя с ID: {}", userId);

        userService.findById(userId);

        return eventStorage.findEventsByUserId(userId).stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    public void saveEvent(Long userId, Long entityId, EventType eventType, EventOperation operation) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(entityId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setTimestamp(java.time.Instant.now().toEpochMilli());

        eventStorage.saveEvent(event);
    }
}