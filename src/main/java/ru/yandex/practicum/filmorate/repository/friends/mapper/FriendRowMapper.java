package ru.yandex.practicum.filmorate.repository.friends.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendRowMapper implements RowMapper<Friend> {
    @Override
    public Friend mapRow(ResultSet rs, int rowNum) throws SQLException {
        long userId = rs.getLong("user_id");
        long friendId = rs.getLong("friend_id");
        return new Friend(userId, friendId);
    }
}
