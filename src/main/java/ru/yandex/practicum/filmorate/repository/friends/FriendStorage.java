package ru.yandex.practicum.filmorate.repository.friends;

import java.util.Set;

public interface FriendStorage {

    boolean addFriends(Long userId, Long friendId);

    boolean deleteFriends(Long userId, Long friendId);

    Set<Long> getFriends(Long userId);

    Set<Long> getCommonFriends(Long userId, Long otherId);
}
