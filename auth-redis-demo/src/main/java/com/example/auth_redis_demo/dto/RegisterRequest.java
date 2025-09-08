package com.example.auth_redis_demo.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String role; // optional, e.g., "ADMIN", "SUPERADMIN"
}
