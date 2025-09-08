package com.example.auth_redis_demo.repository;

import com.example.auth_redis_demo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name); // ✅ ensures we can check existence

}
