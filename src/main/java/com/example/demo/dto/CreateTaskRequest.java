package com.example.demo.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public class CreateTaskRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String title;
    private String description;
    @NotNull
    @FutureOrPresent
    private OffsetDateTime targetDate;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OffsetDateTime getTargetDate() { return targetDate; }
    public void setTargetDate(OffsetDateTime targetDate) { this.targetDate = targetDate; }
}
