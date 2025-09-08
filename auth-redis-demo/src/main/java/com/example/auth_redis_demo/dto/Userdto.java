package com.example.auth_redis_demo.dto;

import java.util.Set;

public record Userdto(
        Long id,
        String username,
        String firstname,
        String lastname,
        Set<Roledto> roles
) {}

