package com.example.auth_redis_demo.dto;

import lombok.Data;

@Data
public class RoleAssignRequest {
    private Long userId;
    private Long roleId;
}
