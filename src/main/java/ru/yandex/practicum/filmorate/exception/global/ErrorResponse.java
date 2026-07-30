package ru.yandex.practicum.filmorate.exception.global;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    @JsonProperty("error")
    private final String code;
    private final String message;
}
