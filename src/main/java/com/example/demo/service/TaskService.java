package com.example.demo.service;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.repo.TaskRepository;
import com.example.demo.messaging.TaskEventPublisher;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

import static com.example.demo.config.CacheConfig.CACHE_TASKS_ALL;
import static com.example.demo.config.CacheConfig.CACHE_TASKS_PENDING;

@Service
public class TaskService {
    private final TaskRepository tasks;
    private final UserService userService;
    private final TaskEventPublisher eventPublisher;

    public TaskService(TaskRepository tasks, UserService userService, TaskEventPublisher eventPublisher) {
        this.tasks = tasks;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @CacheEvict(cacheNames = {CACHE_TASKS_ALL, CACHE_TASKS_PENDING}, key = "#req.userId")
    public Task create(CreateTaskRequest req) {
        userService.requireUser(req.getUserId());
        Task t = new Task();
        t.setUserId(req.getUserId());
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setCreatedAt(OffsetDateTime.now());
        t.setTargetDate(req.getTargetDate());
        t.setStatus(TaskStatus.PENDING);
        t.setDeleted(false);
        Task saved = tasks.save(t);
        eventPublisher.publishTaskCreated(saved);
        return saved;
    }

    @Cacheable(cacheNames = CACHE_TASKS_ALL, key = "#userId")
    public List<Task> findAllByUser(Long userId) {
        userService.requireUser(userId);
        return tasks.findByUserIdAndDeletedFalse(userId);
    }

    @Cacheable(cacheNames = CACHE_TASKS_PENDING, key = "#userId")
    public List<Task> findPendingByUser(Long userId) {
        userService.requireUser(userId);
        return tasks.findByUserIdAndDeletedFalseAndStatus(userId, TaskStatus.PENDING);
    }

    @CacheEvict(cacheNames = {CACHE_TASKS_ALL, CACHE_TASKS_PENDING}, key = "#userId")
    public void softDelete(Long userId, Long taskId) {
        userService.requireUser(userId);
        Task t = tasks.findById(taskId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        if (!t.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        if (!t.isDeleted()) {
            t.setDeleted(true);
            Task saved = tasks.save(t);
            eventPublisher.publishTaskDeleted(saved);
        }
    }
}
