package com.example.auth_redis_demo.service;

import com.example.auth_redis_demo.model.Role;
import com.example.auth_redis_demo.model.User;
import com.example.auth_redis_demo.repository.RoleRepository;
import com.example.auth_redis_demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // ================= Assign Role by IDs =================
    @Transactional
    public Map<String, String> assignRoleToUser(Long userId, Long roleId) {

        // Get current authenticated user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String assignedBy;
        Set<String> currentRoles = new HashSet<>();

        if (principal instanceof UserDetails userDetails) {
            assignedBy = userDetails.getUsername();
            userDetails.getAuthorities()
                    .forEach(a -> currentRoles.add(a.getAuthority().replace("ROLE_", "")));
        } else if (principal instanceof String username) {
            assignedBy = username;
            currentRoles.add("SUPERADMIN"); // assume SUPERADMIN for testing
        } else {
            throw new RuntimeException("Cannot determine authenticated user");
        }

        // Load target user
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Load role
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        // Role hierarchy check
        if (currentRoles.contains("SUPERADMIN")) {
            // SUPERADMIN can assign any role
        } else if (currentRoles.contains("ADMIN")) {
            if (!role.getName().equals("USER")) {
                throw new RuntimeException("ADMIN can assign only USER role");
            }
        } else {
            throw new RuntimeException("You don't have permission to assign roles");
        }

        // Assign role safely
        targetUser.getRoles().add(role);
        userRepository.save(targetUser);

        // Prepare response with names
        Map<String, String> response = new HashMap<>();
        response.put("assignedTo", targetUser.getUsername());
        response.put("assignedBy", assignedBy);
        response.put("roleName", role.getName());

        return response;
    }

    // ================= Remove Role by IDs =================
    @Transactional
    public Map<String, String> removeRoleFromUser(Long userId, Long roleId) {

        // Load target user
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Load role
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        if (!targetUser.getRoles().contains(role)) {
            throw new RuntimeException("User does not have this role");
        }

        targetUser.getRoles().remove(role);
        userRepository.save(targetUser);

        // Get current authenticated user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String removedBy;
        if (principal instanceof UserDetails userDetails) {
            removedBy = userDetails.getUsername();
        } else if (principal instanceof String username) {
            removedBy = username;
        } else {
            throw new RuntimeException("Cannot determine authenticated user");
        }

        // Prepare response with names
        Map<String, String> response = new HashMap<>();
        response.put("removedFrom", targetUser.getUsername());
        response.put("removedBy", removedBy);
        response.put("roleName", role.getName());

        return response;
    }
}
