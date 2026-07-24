package ru.yandex.practicum.filmorate.repository.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LikeRepository implements LikeStorage {

    private final Set<Like> likes = new HashSet<>();

    @Override
    public Set<Long> getUserIds(Long filmId) {
        return likes.stream()
                .filter(like -> Objects.equals(like.getFilmId(), filmId))
                .map(Like::getUserId)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        log.debug("Пользователь ID {} добавил лайк фильму ID {}", userId, filmId);
        return likes.add(new Like(filmId, userId));
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        return likes.remove(new Like(filmId, userId));
    }
}
