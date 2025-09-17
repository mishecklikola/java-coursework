package com.example.demo.scheduler;

import com.example.demo.messaging.TaskEventPublisher;
import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.repo.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class OverdueTaskScheduler {

    private final TaskRepository taskRepository;
    private final TaskEventPublisher publisher;

    public OverdueTaskScheduler(TaskRepository taskRepository, TaskEventPublisher publisher) {
        this.taskRepository = taskRepository;
        this.publisher = publisher;
    }

    @Transactional
    @Scheduled(fixedDelayString = "${app.scheduler.overdue-check.fixed-delay-ms:60000}",
            initialDelayString = "${app.scheduler.overdue-check.initial-delay-ms:10000}")
    public void checkOverdueTasks() {
        OffsetDateTime now = OffsetDateTime.now();
        List<Task> overdue = taskRepository
                .findByStatusAndDeletedFalseAndTargetDateBeforeAndOverdueNotifiedFalse(TaskStatus.PENDING, now);

        for (Task t : overdue) {
            t.setOverdueNotified(true);
        }
        if (!overdue.isEmpty()) {
            taskRepository.saveAll(overdue);
            overdue.forEach(publisher::publishTaskOverdue);
        }
    }
}
