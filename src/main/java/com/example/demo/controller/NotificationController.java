package com.example.demo.controller;

import com.example.demo.model.Notification;
import com.example.demo.service.NotificationService;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notifications;
    private final UserService users;

    public NotificationController(NotificationService notifications, UserService users) {
        this.notifications = notifications;
        this.users = users;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAll(@RequestParam Long userId) {
        users.requireUser(userId);
        return ResponseEntity.ok(notifications.findAllByUser(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Notification>> getPending(@RequestParam Long userId) {
        users.requireUser(userId);
        return ResponseEntity.ok(notifications.findPendingByUser(userId));
    }
}
