package com.example.demo.controller;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService tasks;

    public TaskController(TaskService tasks) {
        this.tasks = tasks;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAll(@RequestParam Long userId) {
        return ResponseEntity.ok(tasks.findAllByUser(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Task>> getPending(@RequestParam Long userId) {
        return ResponseEntity.ok(tasks.findPendingByUser(userId));
    }

    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody CreateTaskRequest req) {
        Task created = tasks.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId, @RequestParam Long userId) {
        tasks.softDelete(userId, taskId);
        return ResponseEntity.noContent().build();
    }
}
