package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class EventDto {

    private long timestamp;
    private Long userId;
    private String eventType;
    private String operation;
    private Long eventId;
    private Long entityId;
}