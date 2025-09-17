package com.example.demo.controller;

import com.example.demo.DemoApplication;
import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.CreateUserRequest;
import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TaskControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired UserService userService;
    @Autowired TaskService taskService;
    Long userId;

    @BeforeEach
    void setUp() {
        CreateUserRequest r = new CreateUserRequest();
        r.setName("U");
        r.setEmail("u@test.com");
        r.setPassword("p");
        userId = userService.register(r).getId();
    }

    @Test
    void createTask201AndValidate() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest();
        req.setUserId(userId);
        req.setTitle("T1");
        req.setDescription("d");
        req.setTargetDate(OffsetDateTime.now().plusDays(1));

        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.deleted", is(false)));

        req.setTargetDate(OffsetDateTime.now().minusDays(1));
        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listPendingAndSoftDelete() throws Exception {
        Task t1 = taskService.create(new CreateTaskRequest() {{
            setUserId(userId);
            setTitle("A");
            setDescription("");
            setTargetDate(OffsetDateTime.now().plusDays(2));
        }});

        mvc.perform(get("/api/tasks")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mvc.perform(delete("/api/tasks/{id}", t1.getId())
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/tasks")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
