package ru.yandex.practicum.filmorate.service.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Утилитарный класс
 */
@Slf4j
public final class Updater {

    private Updater() {
    }

    /**
     * Метод для обновления полей моделей
     */
    public static <T> void updateField(Logger log,
                                       Long id,
                                       String entityType,
                                       String fieldName,
                                       T newValue,
                                       T oldValue,
                                       Consumer<T> setter) {

        if (newValue == null) {
            return;
        }
        if (!Objects.equals(oldValue, newValue)) {
            setter.accept(newValue);
            log.debug("{} ID {}: поле '{}' изменено на {}", entityType,
                    id, fieldName, newValue);
        }
    }
}
