package com.example.demo.controller;

import com.example.demo.model.Notification;
import com.example.demo.model.NotificationStatus;
import com.example.demo.service.NotificationService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NotificationController.class)
class NotificationControllerTest {
    @Autowired MockMvc mvc;

    @MockBean NotificationService notificationService;
    @MockBean UserService userService;

    @Test
    void getAllAndPending() throws Exception {
        Mockito.doReturn(null).when(userService).requireUser(1L);
        List<Notification> all = List.of(
                new Notification(1L, 1L, "task created", OffsetDateTime.now(), NotificationStatus.PENDING),
                new Notification(2L, 1L, "custom", OffsetDateTime.now(), NotificationStatus.SENT)
        );
        Mockito.when(notificationService.findAllByUser(1L)).thenReturn(all);
        Mockito.when(notificationService.findPendingByUser(1L))
                .thenReturn(List.of(all.get(0)));

        mvc.perform(get("/api/notifications").param("userId","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mvc.perform(get("/api/notifications/pending").param("userId","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is("PENDING")));
    }
}
