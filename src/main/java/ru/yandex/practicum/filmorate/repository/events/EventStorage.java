package ru.yandex.practicum.filmorate.repository.events;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {

    List<Event> findEventsByUserId(Long userId);

    void saveEvent(Event event);
}
