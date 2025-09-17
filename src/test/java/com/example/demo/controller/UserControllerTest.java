package com.example.demo.controller;

import com.example.demo.DemoApplication;
import com.example.demo.dto.CreateUserRequest;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired UserService userService;

    @Test
    void registerReturns201AndHidesPassword() throws Exception {
        CreateUserRequest r = new CreateUserRequest();
        r.setName("Alice");
        r.setEmail("alice@test.com");
        r.setPassword("secret");
        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(r)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void loginOkAndUnauthorized() throws Exception {
        CreateUserRequest r = new CreateUserRequest();
        r.setName("Bob");
        r.setEmail("bob@test.com");
        r.setPassword("p");
        userService.register(r);

        mvc.perform(get("/api/users/login")
                        .param("email","bob@test.com")
                        .param("password","p"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("bob@test.com")));

        mvc.perform(get("/api/users/login")
                        .param("email","bob@test.com")
                        .param("password","x"))
                .andExpect(status().isUnauthorized());
    }
}
