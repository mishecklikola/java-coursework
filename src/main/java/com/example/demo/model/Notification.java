package com.example.demo.model;

import java.time.OffsetDateTime;

public class Notification {
    private Long id;
    private Long userId;
    private String message;
    private OffsetDateTime createdAt;
    private NotificationStatus status;

    public Notification() {}
    public Notification(Long id, Long userId, String message, OffsetDateTime createdAt, NotificationStatus status) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
        this.status = status;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }
}
