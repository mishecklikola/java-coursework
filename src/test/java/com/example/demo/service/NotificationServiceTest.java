package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.model.NotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {
    NotificationService service;

    @BeforeEach
    void setUp() {
        service = new NotificationService();
    }

    @Test
    void createAndFilter() {
        Notification n1 = service.create(1L, "m1");
        Notification n2 = service.create(1L, "m2");
        n2.setStatus(NotificationStatus.SENT);

        List<Notification> all = service.findAllByUser(1L);
        List<Notification> pending = service.findPendingByUser(1L);

        assertEquals(2, all.size());
        assertEquals(1, pending.size());
        assertEquals("m1", pending.get(0).getMessage());
    }
}
