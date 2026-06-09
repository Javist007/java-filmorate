package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.service.util.Updater;

import java.util.Collection;

/**
 * Бизнес‑слой для пользователей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    public static final String DUPLICATE_EMAIL = "Данная электронная почта уже используется";
    public static final String DUPLICATE_LOGIN = "Данный логин уже используется";
    public static final String USER = "Пользователь";
    private final UserRepository repo;

    public Collection<User> findAll() {
        log.info("Получаем список всех пользователей");
        return repo.findAll();
    }

    public User create(User user) {

        if (repo.loginExists(user)) {
            log.warn("Регистрация с уже используемым логином: {}", user.getLogin());
            throw new DuplicateException(DUPLICATE_LOGIN);
        }
        if (repo.emailExists(user)) {
            log.warn("Регистрация с уже используемой почтой: {}", user.getEmail());
            throw new DuplicateException(DUPLICATE_EMAIL);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        User saved = repo.save(user);
        log.info("Пользователь: {} успешно зарегистрирован. ID {}", user.getLogin(), saved.getId());
        return saved;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.warn("Отсутствует ID у объекта. Данные: {}", newUser);
            throw new NotFoundException("ID должен быть указан");
        }

        User existing = repo.findById(newUser.getId())
                .orElseThrow(() -> {
                    log.error("Пользователь не найден. ID: {}", newUser.getId());
                    return new NotFoundException(
                            "Пользователь ID: = " + newUser.getId() + " не найден");
                });

        Updater.updateField(log, existing.getId(), USER, "name", newUser.getName(), existing.getName(),
                existing::setName);

        Updater.updateField(log, existing.getId(), USER, "birthday", newUser.getBirthday(),
                existing.getBirthday(), existing::setBirthday);

        if (newUser.getEmail() != null && !existing.getEmail().equals(newUser.getEmail())) {
            if (repo.emailExists(newUser)) {
                throw new DuplicateException(DUPLICATE_EMAIL);
            }
            existing.setEmail(newUser.getEmail());
            log.info("Пользователь {} сменил email на {}", existing.getLogin(), newUser.getEmail());
        }

        if (newUser.getLogin() != null && !existing.getLogin().equals(newUser.getLogin())) {
            if (repo.loginExists(newUser)) {
                throw new DuplicateException(DUPLICATE_LOGIN);
            }
            existing.setLogin(newUser.getLogin());
            log.info("Пользователь {} сменил login на {}", existing.getId(), newUser.getLogin());
        }

        return repo.save(existing);
    }
}
