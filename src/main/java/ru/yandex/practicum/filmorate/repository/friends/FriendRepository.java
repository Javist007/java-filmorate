package ru.yandex.practicum.filmorate.repository.friends;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FriendRepository implements FriendStorage {

    private final Set<Friend> friendStorage = new HashSet<>();

    @Override
    public boolean addFriends(Long userId, Long friendId) {
        return friendStorage.add(new Friend(userId, friendId)) &&
                friendStorage.add(new Friend(friendId, userId));
    }

    @Override
    public boolean deleteFriends(Long userId, Long friendId) {
        return friendStorage.remove(new Friend(userId, friendId)) &&
                friendStorage.remove(new Friend(friendId, userId));
    }

    @Override
    public Set<Long> getFriends(Long userId) {
        return friendStorage.stream()
                .filter(f -> f.getUserId() == userId)
                .map(Friend::getFriendId)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Long> getCommonFriends(Long userId, Long otherId) {
        Set<Long> friendsA = getFriends(userId);
        Set<Long> friendsB = getFriends(otherId);

        return friendsA.stream()
                .filter(friendsB::contains)
                .collect(Collectors.toUnmodifiableSet());
    }
}
