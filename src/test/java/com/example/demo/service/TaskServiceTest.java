package com.example.demo.service;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.CreateUserRequest;
import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TaskServiceTest {
    @Autowired UserService userService;
    @Autowired NotificationService notificationService;
    @Autowired TaskService taskService;
    Long userId;

    @BeforeEach
    void setUp() {
        CreateUserRequest r = new CreateUserRequest();
        r.setName("T");
        r.setEmail("t@example.com");
        r.setPassword("p");
        userId = userService.register(r).getId();
    }

    @Test
    void createAndFind() {
        CreateTaskRequest req = new CreateTaskRequest();
        req.setUserId(userId);
        req.setTitle("Task1");
        req.setDescription("d");
        req.setTargetDate(OffsetDateTime.now().plusDays(1));
        Task t = taskService.create(req);
        assertNotNull(t.getId());
        assertEquals(TaskStatus.PENDING, t.getStatus());
        List<Task> all = taskService.findAllByUser(userId);
        assertEquals(1, all.size());
    }

    @Test
    void softDeleteHidesFromGet() {
        CreateTaskRequest req = new CreateTaskRequest();
        req.setUserId(userId);
        req.setTitle("X");
        req.setDescription("");
        req.setTargetDate(OffsetDateTime.now().plusDays(1));
        Task t = taskService.create(req);
        taskService.softDelete(userId, t.getId());
        assertTrue(taskService.findAllByUser(userId).isEmpty());
    }

    @Test
    void deleteNotOwnedOrMissing404() {
        assertThrows(Exception.class, () -> taskService.softDelete(userId, 999L));
    }

    @Test
    void createRequiresExistingUser() {
        CreateTaskRequest req = new CreateTaskRequest();
        req.setUserId(777L);
        req.setTitle("A");
        req.setTargetDate(OffsetDateTime.now().plusDays(1));
        assertThrows(Exception.class, () -> taskService.create(req));
    }
}
