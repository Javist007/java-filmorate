package ru.yandex.practicum.filmorate.repository.like;

import java.util.Set;

public interface LikeStorage {

    Set<Long> getUserIds(Long filmId);

    boolean addLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId);

}
