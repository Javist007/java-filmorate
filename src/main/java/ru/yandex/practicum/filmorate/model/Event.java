package ru.yandex.practicum.filmorate.model;

import lombok.Data;

/**
 * Модель ленты событий.
 */
@Data
public class Event {

    private Long eventId;
    private Long userId;
    private Long entityId;
    private EventType eventType;
    private EventOperation operation;
    private long timestamp;
}
