package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String BASE_URL = "/users";
    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UserService userService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Test
    void getUsers_ShouldReturnOk() throws Exception {
        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk());
    }

    @ParameterizedTest(name = "createUser_WithInvalidEmail_{index}")
    @MethodSource("invalidEmailProvider")
    void createUser_WithInvalidEmail_ShouldReturnBadRequest(User user) throws Exception {
        performPost(user);
    }

    @ParameterizedTest(name = "createUser_WithInvalidLogin_{index}")
    @MethodSource("invalidLoginProvider")
    void createUser_WithInvalidLogin_ShouldReturnBadRequest(User user) throws Exception {
        performPost(user);
    }

    @Test
    void createUser_WithMissingName_ShouldDefaultToLogin() throws Exception {
        User user = buildValidUser();
        user.setName(null);
        nameInLogin();
        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsString(user))
                        .contentType(JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user.getLogin())));
    }

    @Test
    void createUser_WithBirthdayInFuture_ShouldReturnBadRequest() throws Exception {
        User user = buildValidUser();
        user.setBirthday(LocalDate.now().plusDays(1));
        performPost(user);
    }

    /**
     * Универсальный метод для POST‑запроса.
     */
    private void performPost(Object payload) throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .content(mapper.writeValueAsString(payload))
                        .contentType(JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    /**
     * Правильный пользователь.
     */
    private static User buildValidUser() {
        return User.builder()
                .email("user@example.com")
                .login("validLogin")
                .name("John Doe")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    private void nameInLogin() {
        when(userService.create(ArgumentMatchers.any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            if (u.getName() == null || u.getName().isBlank()) {
                u.setName(u.getLogin());
            }
            return u;
        });
    }

    static Stream<User> invalidEmailProvider() {
        return Stream.of(
                User.builder()
                        .email("invalid-email")
                        .login("login")
                        .birthday(LocalDate.now())
                        .build(),
                User.builder()
                        .login("login")
                        .birthday(LocalDate.now())
                        .build()
        );
    }

    static Stream<User> invalidLoginProvider() {
        return Stream.of(
                User.builder()
                        .email("user@example.com")
                        .login("")
                        .birthday(LocalDate.now())
                        .build(),
                User.builder()
                        .email("user@example.com")
                        .login("ab")
                        .birthday(LocalDate.now())
                        .build(),
                User.builder()
                        .email("user@example.com")
                        .login("login with space")
                        .birthday(LocalDate.now())
                        .build()
        );
    }
}
