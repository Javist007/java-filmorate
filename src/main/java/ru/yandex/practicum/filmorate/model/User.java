package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Модель пользователя.
 */
@Data
@Builder
@EqualsAndHashCode(of = "id")
public class User {

    private static final String LOGIN_REGEXP = "^[а-яА-яa-zA-Z0-9-_.]{6,12}$";
    private Long id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    @Pattern(regexp = LOGIN_REGEXP,
            message = "Логин должен содержать (6-12) символов (без пробелов) и состоять из: букв и цифр.")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
