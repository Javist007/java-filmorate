package ru.yandex.practicum.filmorate.repository.friends.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.repository.BaseStorage;
import ru.yandex.practicum.filmorate.repository.friends.FriendStorage;

import java.util.HashSet;
import java.util.Set;

@Repository
@Slf4j
@Primary
public class FriendsDBRepository extends BaseStorage<Friend> implements FriendStorage {

    public FriendsDBRepository(JdbcTemplate jdbc, RowMapper<Friend> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Set<Long> getFriends(Long userId) {
        return new HashSet<>(jdbc.query(FriendsSQLRequests.FIND_FRIEND,
                (resultSet, rowNumber) -> resultSet.getLong("friend_id"), userId));
    }

    @Override
    public boolean addFriends(Long userId, Long friendId) {
        int update = jdbc.update(FriendsSQLRequests.ADD_FRIEND, userId, friendId);
        return update > 0;
    }

    @Override
    public boolean deleteFriends(Long userId, Long friendId) {
        int update = jdbc.update(FriendsSQLRequests.REMOVE_FRIEND, userId, friendId);
        return update > 0;
    }

    @Override
    public Set<Long> getCommonFriends(Long userId, Long otherId) {
        return new HashSet<>(jdbc.query(FriendsSQLRequests.FIND_COMMON_FRIENDS,
                (resultSet, rowNumber) -> resultSet.getLong("friend_id"), userId, otherId));
    }
}
