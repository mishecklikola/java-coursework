package com.example.demo.messaging;

import com.example.demo.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TaskEventListener {

    private final NotificationService notificationService;

    public TaskEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async("taskExecutor")
    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onTaskEvent(TaskEvent event) {
        if (event == null || event.getType() == null) return;

        switch (event.getType()) {
            case TASK_CREATED -> notificationService.create(
                    event.getUserId(), "Task created: " + event.getTitle());
            case TASK_DELETED -> notificationService.create(
                    event.getUserId(), "Task deleted: " + event.getTitle());
            case TASK_OVERDUE -> notificationService.create(
                    event.getUserId(), "Task overdue: " + event.getTitle());
        }
    }
}
