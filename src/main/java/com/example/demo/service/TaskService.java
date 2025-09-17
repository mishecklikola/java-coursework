package com.example.demo.service;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private final ConcurrentHashMap<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(1);
    private final UserService userService;
    private final NotificationService notificationService;

    public TaskService(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public Task create(CreateTaskRequest req) {
        userService.requireUser(req.getUserId());
        Long id = idSeq.getAndIncrement();
        Task task = new Task(id, req.getUserId(), req.getTitle(), req.getDescription(), OffsetDateTime.now(), req.getTargetDate(), TaskStatus.PENDING, false);
        tasks.put(id, task);
        notificationService.create(req.getUserId(), "Task created: " + task.getTitle());
        return task;
    }

    public List<Task> findAllByUser(Long userId) {
        userService.requireUser(userId);
        List<Task> list = new ArrayList<>();
        for (Task t : tasks.values()) if (t.getUserId().equals(userId) && !t.isDeleted()) list.add(t);
        return list;
    }

    public List<Task> findPendingByUser(Long userId) {
        userService.requireUser(userId);
        List<Task> list = new ArrayList<>();
        for (Task t : tasks.values()) if (t.getUserId().equals(userId) && !t.isDeleted() && t.getStatus() == TaskStatus.PENDING) list.add(t);
        return list;
    }

    public void softDelete(Long userId, Long taskId) {
        userService.requireUser(userId);
        Task t = tasks.get(taskId);
        if (t == null || !t.getUserId().equals(userId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        if (!t.isDeleted()) {
            t.setDeleted(true);
            notificationService.create(userId, "Task deleted: " + t.getTitle());
        }
    }
}
