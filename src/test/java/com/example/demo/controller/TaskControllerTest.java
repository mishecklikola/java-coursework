package com.example.demo.controller;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TaskController.class)
class TaskControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean TaskService taskService;

    @Test
    void createTask201AndValidate() throws Exception {
        Task t = new Task(10L, 1L, "T1", "d", OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1), TaskStatus.PENDING, false);
        Mockito.when(taskService.create(ArgumentMatchers.any(CreateTaskRequest.class))).thenReturn(t);

        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"userId":1,"title":"T1","description":"d","targetDate":"2030-01-01T10:00:00Z"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.deleted", is(false)));
    }

    @Test
    void listPendingAndSoftDelete() throws Exception {
        Task t = new Task(10L, 1L, "A", "", OffsetDateTime.now(), OffsetDateTime.now().plusDays(2), TaskStatus.PENDING, false);
        Mockito.when(taskService.findAllByUser(1L)).thenReturn(List.of(t));

        mvc.perform(get("/api/tasks").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mvc.perform(delete("/api/tasks/{id}", 10L).param("userId","1"))
                .andExpect(status().isNoContent());
        Mockito.verify(taskService).softDelete(1L, 10L);
    }
}
