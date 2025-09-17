package com.example.demo.messaging;

import com.example.demo.model.Task;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import static com.example.demo.messaging.RabbitConfig.EXCHANGE;

@Component
public class TaskEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public TaskEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishTaskCreated(Task task) {
        TaskEvent event = new TaskEvent(
                TaskEventType.TASK_CREATED,
                task.getUserId(),
                task.getId(),
                task.getTitle(),
                OffsetDateTime.now()
        );
        rabbitTemplate.convertAndSend(EXCHANGE, "task.created", event);
    }

    public void publishTaskDeleted(Task task) {
        TaskEvent event = new TaskEvent(
                TaskEventType.TASK_DELETED,
                task.getUserId(),
                task.getId(),
                task.getTitle(),
                OffsetDateTime.now()
        );
        rabbitTemplate.convertAndSend(EXCHANGE, "task.deleted", event);
    }
}
