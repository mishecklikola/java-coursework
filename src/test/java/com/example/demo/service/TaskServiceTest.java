package com.example.demo.service;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.messaging.TaskEventPublisher;
import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.repo.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock TaskRepository taskRepository;
    @Mock UserService userService;
    @Mock TaskEventPublisher eventPublisher;

    @InjectMocks TaskService taskService;

    Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        lenient().doReturn(null).when(userService).requireUser(userId);
    }

    @Test
    void createAndFind() {
        CreateTaskRequest req = new CreateTaskRequest();
        req.setUserId(userId);
        req.setTitle("Task1");
        req.setDescription("d");
        req.setTargetDate(OffsetDateTime.now().plusDays(1));

        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });
        when(taskRepository.findByUserIdAndDeletedFalse(userId))
                .thenReturn(List.of(new Task(10L, userId, "Task1", "d", OffsetDateTime.now(),
                        req.getTargetDate(), TaskStatus.PENDING, false)));

        Task t = taskService.create(req);
        assertEquals(10L, t.getId());
        assertEquals(TaskStatus.PENDING, t.getStatus());
        verify(eventPublisher).publishTaskCreated(argThat(x -> x != null && x.getId().equals(10L)));

        List<Task> all = taskService.findAllByUser(userId);
        assertEquals(1, all.size());
    }

    @Test
    void softDeleteFlow() {
        Task t = new Task(10L, userId, "X", "", OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1), TaskStatus.PENDING, false);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(t));
        // ВАЖНО: save должен вернуть объект, который уйдёт в publishTaskDeleted(...)
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        taskService.softDelete(userId, 10L);

        assertTrue(t.isDeleted());
        verify(taskRepository).save(t);
        verify(eventPublisher).publishTaskDeleted(argThat(x -> x != null && x.getId().equals(10L)));
    }

    @Test
    void softDeleteNotFoundOrNotOwned() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> taskService.softDelete(userId, 999L));

        Task other = new Task(11L, 777L, "Y","", OffsetDateTime.now(), OffsetDateTime.now().plusDays(1), TaskStatus.PENDING, false);
        when(taskRepository.findById(11L)).thenReturn(Optional.of(other));
        assertThrows(ResponseStatusException.class, () -> taskService.softDelete(userId, 11L));
    }

    @Test
    void createRequiresExistingUser() {
        CreateTaskRequest req = new CreateTaskRequest();
        req.setUserId(777L);
        req.setTitle("A");
        req.setTargetDate(OffsetDateTime.now().plusDays(1));
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND))
                .when(userService).requireUser(777L);
        assertThrows(ResponseStatusException.class, () -> taskService.create(req));
        verify(taskRepository, never()).save(any());
        verify(eventPublisher, never()).publishTaskCreated(any());
    }
}
