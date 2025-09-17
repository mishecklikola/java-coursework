package com.example.demo.repo;

import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdAndDeletedFalse(Long userId);
    List<Task> findByUserIdAndDeletedFalseAndStatus(Long userId, TaskStatus status);

    List<Task> findByStatusAndDeletedFalseAndTargetDateBeforeAndOverdueNotifiedFalse(
            TaskStatus status, OffsetDateTime threshold);
}
