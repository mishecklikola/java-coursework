package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.model.NotificationStatus;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class NotificationService {
    private final ConcurrentHashMap<Long, Notification> notifications = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(1);

    public Notification create(Long userId, String message) {
        Long id = idSeq.getAndIncrement();
        Notification n = new Notification(id, userId, message, OffsetDateTime.now(), NotificationStatus.PENDING);
        notifications.put(id, n);
        return n;
    }

    public List<Notification> findAllByUser(Long userId) {
        List<Notification> list = new ArrayList<>();
        for (Notification n : notifications.values()) if (n.getUserId().equals(userId)) list.add(n);
        return list;
    }

    public List<Notification> findPendingByUser(Long userId) {
        List<Notification> list = new ArrayList<>();
        for (Notification n : notifications.values()) if (n.getUserId().equals(userId) && n.getStatus() == NotificationStatus.PENDING) list.add(n);
        return list;
    }
}
