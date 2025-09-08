package com.example.auth_redis_demo.dto;

import lombok.Data;

@Data
public class AssignRoleRequest {
    private String username; // user to assign role
    private String role;     // role to assign
}
