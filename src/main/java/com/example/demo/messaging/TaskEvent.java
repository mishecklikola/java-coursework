package com.example.demo.messaging;

import java.time.OffsetDateTime;

public class TaskEvent {
    private TaskEventType type;
    private Long userId;
    private Long taskId;
    private String title;
    private OffsetDateTime occurredAt;

    public TaskEvent() {}

    public TaskEvent(TaskEventType type, Long userId, Long taskId, String title, OffsetDateTime occurredAt) {
        this.type = type;
        this.userId = userId;
        this.taskId = taskId;
        this.title = title;
        this.occurredAt = occurredAt;
    }

    public TaskEventType getType() { return type; }
    public void setType(TaskEventType type) { this.type = type; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
}
