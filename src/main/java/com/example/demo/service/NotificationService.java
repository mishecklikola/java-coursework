package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.model.NotificationStatus;
import com.example.demo.repo.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notifications;

    public NotificationService(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    public Notification create(Long userId, String message) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setMessage(message);
        n.setCreatedAt(OffsetDateTime.now());
        n.setStatus(NotificationStatus.PENDING);
        return notifications.save(n);
    }

    public Notification update(Notification notification) {
        return notifications.save(notification);
    }

    public List<Notification> findAllByUser(Long userId) {
        return notifications.findByUserId(userId);
    }

    public List<Notification> findPendingByUser(Long userId) {
        return notifications.findByUserIdAndStatus(userId, NotificationStatus.PENDING);
    }
}
