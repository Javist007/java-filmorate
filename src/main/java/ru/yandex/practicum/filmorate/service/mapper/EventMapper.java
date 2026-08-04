package ru.yandex.practicum.filmorate.service.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

/**
 * Вспомогательный класс для конвертации Event -> DTO.
 */
@UtilityClass
public class EventMapper {

    public static EventDto toDto(Event event) {
        if (event == null) {
            return null;
        }

        EventDto dto = new EventDto();
        dto.setEventId(event.getEventId());
        dto.setTimestamp(event.getTimestamp());
        dto.setUserId(event.getUserId());
        dto.setEventType(event.getEventType().name());
        dto.setOperation(event.getOperation().name());
        dto.setEntityId(event.getEntityId());

        return dto;
    }
}