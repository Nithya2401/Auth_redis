package com.example.auth_redis_demo.service;

import com.example.auth_redis_demo.dto.RegisterRequest;
import com.example.auth_redis_demo.model.Role;
import com.example.auth_redis_demo.model.User;
import com.example.auth_redis_demo.repository.RoleRepository;
import com.example.auth_redis_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // ========================== Register user ==========================
    @Transactional
    public ResponseEntity<Map<String, Object>> registerUser(RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.existsByUsername(request.getUsername())) {
            response.put("status", "error");
            response.put("message", "Username already exists!");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());

        // ✅ If role not provided, assign default "USER"
        String roleName = (request.getRole() == null || request.getRole().isBlank())
                ? "USER"
                : request.getRole();

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    return roleRepository.save(newRole);
                });

        user.setRoles(Set.of(role));
        userRepository.save(user);

        response.put("status", "success");
        response.put("message", "User registered successfully");
        response.put("username", user.getUsername());
        response.put("assignedRole", role.getName());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ========================== Load user for authentication ==========================
    public UserDetails loadUserByUsernameForAuth(String username) {
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + r.getName()))
                        .toList()
        );
    }
}
