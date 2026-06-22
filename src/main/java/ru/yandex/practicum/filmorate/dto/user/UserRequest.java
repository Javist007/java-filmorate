package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Входной DTO для создания/обновления пользователя.
 */
@Data
@EqualsAndHashCode(of = "id")
public class UserRequest {

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
