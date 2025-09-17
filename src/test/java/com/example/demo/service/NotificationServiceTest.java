package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.model.NotificationStatus;
import com.example.demo.repo.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock NotificationRepository notificationRepository;
    @InjectMocks NotificationService service;

    @Test
    void createAndFilter() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> {
            Notification n = inv.getArgument(0);
            if (n.getId() == null) n.setId(1L);
            return n;
        });
        when(notificationRepository.findByUserId(1L))
                .thenReturn(List.of(
                        new Notification(1L, 1L, "m1", null, NotificationStatus.PENDING),
                        new Notification(2L, 1L, "m2", null, NotificationStatus.SENT)
                ));
        when(notificationRepository.findByUserIdAndStatus(1L, NotificationStatus.PENDING))
                .thenReturn(List.of(
                        new Notification(1L, 1L, "m1", null, NotificationStatus.PENDING)
                ));

        Notification n1 = service.create(1L, "m1");
        Notification n2 = service.create(1L, "m2");
        n2.setStatus(NotificationStatus.SENT);
        service.update(n2);

        List<Notification> all = service.findAllByUser(1L);
        List<Notification> pending = service.findPendingByUser(1L);

        assertEquals(2, all.size());
        assertEquals(1, pending.size());
        assertEquals("m1", pending.get(0).getMessage());
        verify(notificationRepository, times(3)).save(any(Notification.class));
    }
}
