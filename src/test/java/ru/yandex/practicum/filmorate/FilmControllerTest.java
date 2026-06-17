package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    private static final String BASE_URL = "/films";
    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public FilmService filmService() {
            return Mockito.mock(FilmService.class);
        }
    }

    @Test
    void getFilms_ShouldReturnOk() throws Exception {
        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk());
    }

    @ParameterizedTest(name = "createFilm_WithInvalidName_{index}")
    @MethodSource("invalidNameProvider")
    void createFilm_WithInvalidName_ShouldReturnBadRequest(Film film) throws Exception {
        performPost(film, HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest(name = "createFilm_WithInvalidDuration_{index}")
    @MethodSource("invalidDurationProvider")
    void createFilm_WithInvalidDuration_ShouldReturnBadRequest(Film film) throws Exception {
        performPost(film, HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest(name = "createFilm_WithInvalidReleaseDate_{index}")
    @MethodSource("invalidReleaseDateProvider")
    void createFilm_WithInvalidReleaseDate_ShouldReturnBadRequest(Film film) throws Exception {
        performPost(film, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createFilm_WithMaxDescriptionLength_ShouldReturnOk() throws Exception {
        Film film = buildValidFilm();

        film.setDescription("a".repeat(200));

        performPost(film, HttpStatus.OK);
    }

    @Test
    void createFilm_WithOverMaxDescriptionLength_ShouldReturnBadRequest() throws Exception {
        Film film = buildValidFilm();

        film.setDescription("a".repeat(201));
        performPost(film, HttpStatus.BAD_REQUEST);
    }

    @Test
    void createFilm_WithMinimumAcceptReleaseDate_ShouldReturnOk() throws Exception {
        Film film = buildValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        performPost(film, HttpStatus.OK);
    }

    @Test
    void createFilm_WithOneDuration_ShouldReturnOk() throws Exception {
        Film film = buildValidFilm();
        film.setDuration(1);
        performPost(film, HttpStatus.OK);
    }

    /**
     * Универсальный метод для POST‑запроса.
     */
    private void performPost(Object payload, HttpStatus status) throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsString(payload))
                        .contentType(JSON))
                .andExpect(status().is(status.value()));
    }

    /**
     * Создаём «правильный» Film.
     */
    private static Film buildValidFilm() {
        return Film.builder()
                .name("XKJt2kJZt3OZ5di")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(1964, 3, 21))
                .duration(82)
                .build();
    }

    static Stream<Film> invalidNameProvider() {
        return Stream.of(
                Film.builder()
                        .name("")
                        .description("desc")
                        .releaseDate(LocalDate.now())
                        .duration(10)
                        .build(),
                Film.builder()
                        .description("desc")
                        .releaseDate(LocalDate.now())
                        .duration(10)
                        .build()
        );
    }

    static Stream<Film> invalidDurationProvider() {
        return Stream.of(
                Film.builder()
                        .name("test")
                        .description("desc")
                        .releaseDate(LocalDate.now())
                        .duration(0)
                        .build(),
                Film.builder()
                        .name("test")
                        .description("desc")
                        .releaseDate(LocalDate.now())
                        .duration(-5)
                        .build()
        );
    }

    static Stream<Film> invalidReleaseDateProvider() {
        return Stream.of(
                Film.builder()
                        .name("test")
                        .description("desc")
                        .releaseDate(LocalDate.of(1895, 12, 27))
                        .duration(10)
                        .build(),
                Film.builder()
                        .name("test")
                        .description("desc")
                        .releaseDate(null)
                        .duration(10)
                        .build()
        );
    }
}
