package com.example.demo.controller;

import com.example.demo.DemoApplication;
import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.CreateUserRequest;
import com.example.demo.model.NotificationStatus;
import com.example.demo.service.NotificationService;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class NotificationControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired UserService userService;
    @Autowired TaskService taskService;
    @Autowired NotificationService notificationService;
    Long userId;

    @BeforeEach
    void setUp() {
        CreateUserRequest r = new CreateUserRequest();
        r.setName("N");
        r.setEmail("n@test.com");
        r.setPassword("p");
        userId = userService.register(r).getId();

        CreateTaskRequest tr = new CreateTaskRequest();
        tr.setUserId(userId);
        tr.setTitle("Task");
        tr.setDescription("");
        tr.setTargetDate(OffsetDateTime.now().plusDays(1));
        taskService.create(tr);

        notificationService.create(userId, "custom").setStatus(NotificationStatus.SENT);
    }

    @Test
    void getAllAndPending() throws Exception {
        mvc.perform(get("/api/notifications")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mvc.perform(get("/api/notifications/pending")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is("PENDING")));
    }
}
