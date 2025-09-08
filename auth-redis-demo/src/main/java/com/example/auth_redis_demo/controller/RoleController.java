package com.example.auth_redis_demo.controller;

import com.example.auth_redis_demo.model.Role;
import com.example.auth_redis_demo.repository.RoleRepository;
import com.example.auth_redis_demo.service.RoleService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RoleRepository roleRepository;

    // ================= Create Role =================
    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Map<String, Object>> createRole(@RequestBody RoleRequest request) {
        Map<String, Object> response = new HashMap<>();
        String roleName = request.getRole();

        if (roleName == null || roleName.isBlank()) {
            response.put("status", "error");
            response.put("message", "Role cannot be empty");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (roleRepository.findByName(roleName).isPresent()) {
            response.put("status", "error");
            response.put("message", "Role already exists: " + roleName);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Role newRole = new Role();
        newRole.setName(roleName);
        roleRepository.saveAndFlush(newRole);

        response.put("status", "success");
        response.put("message", "Role created successfully");
        response.put("role", roleName);
        response.put("roleId", newRole.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ================= Assign Role by IDs =================
    @PostMapping("/assign")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Map<String, Object>> assignRole(@RequestBody AssignRoleRequestById request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> messageMap = roleService.assignRoleToUser(request.getUserId(), request.getRoleId());
            response.put("status", "success");
            response.putAll(messageMap); // contains assignedTo, assignedBy, roleName
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
    }

    // ================= Remove Role by IDs =================
    @PostMapping("/remove")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Map<String, Object>> removeRole(@RequestBody AssignRoleRequestById request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> messageMap = roleService.removeRoleFromUser(request.getUserId(), request.getRoleId());
            response.put("status", "success");
            response.putAll(messageMap); // contains removedFrom, removedBy, roleName
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // ================= List All Roles =================
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    // ====================== Request DTOs ======================
    @Data
    static class RoleRequest {
        private String role;
    }

    @Data
    static class AssignRoleRequestById {
        private Long userId;
        private Long roleId;
    }
}
