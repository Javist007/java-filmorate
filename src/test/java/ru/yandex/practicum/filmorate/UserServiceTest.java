//package ru.yandex.practicum.filmorate;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import ru.yandex.practicum.filmorate.exception.DuplicateException;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.repository.user.UserRepository;
//import ru.yandex.practicum.filmorate.service.UserService;
//
//class UserServiceTest {
//
//    private UserService userService;
//
//    @BeforeEach
//    void setUp() {
//        UserRepository repo = new UserRepository();
//        userService = new UserService(repo);
//    }
//
//    @Test
//    void create_ShouldAssignIdAndSetNameIfBlank() {
//        User u = new User();
//        u.setLogin("login1");
//        u.setEmail("a@b.com");
//
//        User created = userService.create(u);
//        assertThat(created.getId()).isNotNull();
//        assertEquals("login1", created.getName());
//    }
//
//    @Test
//    void create_DuplicateLoginOrEmail_ShouldThrow() {
//        User u1 = new User(); u1.setLogin("dup"); u1.setEmail("e@x.com");
//        userService.create(u1);
//
//        User dupLogin = new User(); dupLogin.setLogin("dup"); dupLogin.setEmail("other@x.com");
//        assertThrows(DuplicateException.class, () -> userService.create(dupLogin));
//
//        User dupEmail = new User(); dupEmail.setLogin("newlogin"); dupEmail.setEmail("e@x.com");
//        assertThrows(DuplicateException.class, () -> userService.create(dupEmail));
//    }
//
//    @Test
//    void update_ShouldChangeEmailAndLoginWithUniquenessChecks() {
//        User u1 = new User(); u1.setLogin("u1"); u1.setEmail("a@b.c");
//        User u2 = new User(); u2.setLogin("u2"); u2.setEmail("x@y.z");
//
//        User s1 = createUser(userService, u1.getLogin(), u1.getEmail());
//        userService.create(u2);
//
//        User upd = new User(); upd.setId(s1.getId()); upd.setEmail("new@b.c");
//        User res = userService.update(upd);
//        assertEquals("new@b.c", res.getEmail());
//
//        upd.setEmail("x@y.z");
//        assertThrows(DuplicateException.class,
//                () -> userService.update(upd));
//
//        upd.setEmail(null);
//        upd.setLogin("u3");
//        res = userService.update(upd);
//        assertEquals("u3", res.getLogin());
//
//        upd.setLogin("u2");
//        assertThrows(DuplicateException.class,
//                () -> userService.update(upd));
//    }
//
//    @Test
//    void addFriend_ShouldCreateBidirectionalRelation() {
//        User sa = createUser(userService, "a", "a@x.com");
//        User sb = createUser(userService, "b", "b@x.com");
//
//        userService.addFriend(sa.getId(), sb.getId());
//        assertTrue(userService.getFriends(sa.getId()).stream()
//                .anyMatch(u -> u.getId().equals(sb.getId())));
//        assertTrue(userService.getFriends(sb.getId()).stream()
//                .anyMatch(u -> u.getId().equals(sa.getId())));
//
//        userService.addFriend(sa.getId(), sb.getId());
//    }
//
//    @Test
//    void addFriend_Self_ShouldThrow() {
//        User a = createUser(userService, "a", "a@x.com");
//        assertThrows(NotFoundException.class,
//                () -> userService.addFriend(a.getId(), a.getId()));
//    }
//
//    @Test
//    void removeFriend_ShouldDeleteBidirectionalRelation() {
//        User a = createUser(userService, "a", "a@x.com");
//        User b = createUser(userService, "b", "b@x.com");
//
//        userService.addFriend(a.getId(), b.getId());
//        userService.removeFriend(a.getId(), b.getId());
//
//        assertFalse(userService.getFriends(a.getId()).stream()
//                .anyMatch(u -> u.getId().equals(b.getId())));
//        assertFalse(userService.getFriends(b.getId()).stream()
//                .anyMatch(u -> u.getId().equals(a.getId())));
//    }
//
//    @Test
//    void getCommonFriends_ShouldReturnOnlyShared() {
//        User sa = createUser(userService, "a", "a@x.com");
//        User sb = createUser(userService, "b", "b@x.com");
//        User sc = createUser(userService, "c", "c@x.com");
//
//        userService.addFriend(sa.getId(), sc.getId());
//        userService.addFriend(sb.getId(), sc.getId());
//
//        List<User> common = userService.getCommonFriends(sa.getId(), sb.getId());
//        assertThat(common).hasSize(1);
//        assertEquals(sc.getId(), common.getFirst().getId());
//    }
//
//    /**
//     * Быстрый «фабричный» метод для создания и сохранения пользователя.
//     *
//     * @param login логин (непустой, без пробелов)
//     * @param email e‑mail (валидный формат)
//     * @return пользователь с присвоенным ID
//     */
//    private static User createUser(UserService service,
//                                   String login,
//                                   String email) {
//        User u = new User();
//        u.setLogin(login);
//        u.setEmail(email);
//        return service.create(u);
//    }
//}
