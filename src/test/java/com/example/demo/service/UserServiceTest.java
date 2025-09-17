package com.example.demo.service;

import com.example.demo.dto.CreateUserRequest;
import com.example.demo.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void registerSuccess() {
        CreateUserRequest r = new CreateUserRequest();
        r.setName("Alice");
        r.setEmail("alice@example.com");
        r.setPassword("secret");
        User u = userService.register(r);
        assertNotNull(u.getId());
        assertEquals("alice@example.com", u.getEmail());
        assertNotNull(u.getCreatedAt());
    }

    @Test
    void registerDuplicateEmail() {
        CreateUserRequest r = new CreateUserRequest();
        r.setName("A");
        r.setEmail("a@example.com");
        r.setPassword("p");
        userService.register(r);
        assertThrows(ResponseStatusException.class, () -> userService.register(r));
    }

    @Test
    void loginOkAndFailures() {
        CreateUserRequest r = new CreateUserRequest();
        r.setName("B");
        r.setEmail("b@example.com");
        r.setPassword("q");
        userService.register(r);
        assertTrue(userService.login("b@example.com","q").isPresent());
        assertTrue(userService.login("b@example.com","wrong").isEmpty());
        assertTrue(userService.login("unknown@example.com","q").isEmpty());
    }
}
