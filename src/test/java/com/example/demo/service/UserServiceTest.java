package com.example.demo.service;

import com.example.demo.dto.CreateUserRequest;
import com.example.demo.model.User;
import com.example.demo.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @InjectMocks UserService userService;

    @Test
    void registerSuccess() {
        CreateUserRequest r = new CreateUserRequest();
        r.setName("Alice");
        r.setEmail("alice@example.com");
        r.setPassword("secret");

        when(userRepository.existsByEmailIgnoreCase("alice@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        User u = userService.register(r);
        assertEquals(1L, u.getId());
        assertEquals("alice@example.com", u.getEmail());
        assertNotNull(u.getCreatedAt());

        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(cap.capture());
        assertEquals("secret", cap.getValue().getPassword());
    }

    @Test
    void registerDuplicateEmail() {
        CreateUserRequest r = new CreateUserRequest();
        r.setName("A");
        r.setEmail("a@example.com");
        r.setPassword("p");
        when(userRepository.existsByEmailIgnoreCase("a@example.com")).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> userService.register(r));
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginOkAndFailures() {
        User u = new User(1L, "B", "b@example.com", "q", OffsetDateTime.now());
        when(userRepository.findByEmailIgnoreCase("b@example.com")).thenReturn(Optional.of(u));

        assertTrue(userService.login("b@example.com", "q").isPresent());
        assertTrue(userService.login("b@example.com", "wrong").isEmpty());
        when(userRepository.findByEmailIgnoreCase("unknown@example.com")).thenReturn(Optional.empty());
        assertTrue(userService.login("unknown@example.com", "q").isEmpty());
    }

    @Test
    void requireUserFoundOr404() {
        User u = new User(1L, "X", "x@ex.com", "p", OffsetDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        assertEquals(1L, userService.requireUser(1L).getId());

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> userService.requireUser(2L));
    }
}
