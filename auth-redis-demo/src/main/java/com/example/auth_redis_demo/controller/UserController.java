package com.example.auth_redis_demo.controller;

import com.example.auth_redis_demo.model.User;
import com.example.auth_redis_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // ================= List all registered users with IDs and roles =================
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")  // Only admins can access
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(u -> new UserResponse(
                        u.getId(),
                        u.getUsername(),
                        u.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    // ================= Response DTO =================
    public record UserResponse(Long id, String username, Set<String> roles) {}
}
