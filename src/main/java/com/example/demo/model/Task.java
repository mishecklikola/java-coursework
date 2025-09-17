package com.example.demo.model;

import java.time.OffsetDateTime;

public class Task {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime targetDate;
    private TaskStatus status;
    private boolean deleted;

    public Task() {}
    public Task(Long id, Long userId, String title, String description, OffsetDateTime createdAt, OffsetDateTime targetDate, TaskStatus status, boolean deleted) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.targetDate = targetDate;
        this.status = status;
        this.deleted = deleted;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getTargetDate() { return targetDate; }
    public void setTargetDate(OffsetDateTime targetDate) { this.targetDate = targetDate; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
