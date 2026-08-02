package ru.yandex.practicum.filmorate.repository.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.repository.BaseStorage;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class EventDBRepository extends BaseStorage<Event> implements EventStorage {

    NamedParameterJdbcTemplate namedJdbc;

    public EventDBRepository(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
        this.namedJdbc = new NamedParameterJdbcTemplate(jdbc);
    }

    @Override
    public List<Event> findEventsByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return namedJdbc.query(
                EventSQLRequests.FIND_BY_USER_ID,
                Map.of("userId", userId),
                mapper
        );
    }

    public void saveEvent(Event event) {
        insert(EventSQLRequests.INSERT_EVENT,
                event.getUserId(),
                event.getEntityId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getTimestamp()
        );
    }
}