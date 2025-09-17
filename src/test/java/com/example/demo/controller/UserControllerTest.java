package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean UserService userService;

    @Test
    void registerReturns201AndHidesPassword() throws Exception {
        User created = new User(1L, "Alice", "alice@test.com", "secret", OffsetDateTime.now());
        Mockito.when(userService.register(any())).thenReturn(created);

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                           {"name":"Alice","email":"alice@test.com","password":"secret"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("alice@test.com")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void loginOkAndUnauthorized() throws Exception {
        User u = new User(2L,"Bob","bob@test.com","p", OffsetDateTime.now());
        Mockito.when(userService.login(eq("bob@test.com"), eq("p"))).thenReturn(Optional.of(u));
        Mockito.when(userService.login(eq("bob@test.com"), eq("x"))).thenReturn(Optional.empty());

        mvc.perform(get("/api/users/login").param("email","bob@test.com").param("password","p"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("bob@test.com")));

        mvc.perform(get("/api/users/login").param("email","bob@test.com").param("password","x"))
                .andExpect(status().isUnauthorized());
    }
}
