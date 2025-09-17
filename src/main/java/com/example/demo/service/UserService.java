package com.example.demo.service;

import com.example.demo.dto.CreateUserRequest;
import com.example.demo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private final Map<Long, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, Long> idByEmail = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(1);

    public User register(CreateUserRequest req) {
        if (idByEmail.containsKey(req.getEmail().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        Long id = idSeq.getAndIncrement();
        User user = new User(id, req.getName(), req.getEmail().toLowerCase(), req.getPassword(), OffsetDateTime.now());
        usersById.put(id, user);
        idByEmail.put(user.getEmail(), id);
        return user;
    }

    public Optional<User> login(String email, String password) {
        Long id = idByEmail.get(email.toLowerCase());
        if (id == null) return Optional.empty();
        User user = usersById.get(id);
        if (user == null) return Optional.empty();
        if (!user.getPassword().equals(password)) return Optional.empty();
        return Optional.of(user);
    }

    public User requireUser(Long userId) {
        User user = usersById.get(userId);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        return user;
    }
}
