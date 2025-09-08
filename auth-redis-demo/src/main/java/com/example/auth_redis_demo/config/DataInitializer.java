package com.example.auth_redis_demo.config;

import com.example.auth_redis_demo.model.Role;
import com.example.auth_redis_demo.model.User;
import com.example.auth_redis_demo.repository.RoleRepository;
import com.example.auth_redis_demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(RoleRepository roleRepo, UserRepository userRepo, BCryptPasswordEncoder encoder) {
        return args -> {
            // ✅ Ensure default roles exist
            Role rUser = roleRepo.findByName("USER")
                    .orElseGet(() -> roleRepo.save(createRole("USER")));
            Role rAdmin = roleRepo.findByName("ADMIN")
                    .orElseGet(() -> roleRepo.save(createRole("ADMIN")));
            Role rSuper = roleRepo.findByName("SUPERADMIN")
                    .orElseGet(() -> roleRepo.save(createRole("SUPERADMIN")));

            // ✅ Ensure default SUPERADMIN user exists
            if (!userRepo.existsByUsername("superadmin")) {
                User s = new User();
                s.setFirstname("System");
                s.setLastname("SuperAdmin");
                s.setUsername("superadmin");
                s.setPassword(encoder.encode("Super@123"));

                Set<Role> roles = new HashSet<>();
                roles.add(rSuper);
                roles.add(rAdmin);
                roles.add(rUser);

                s.setRoles(roles);

                userRepo.save(s);
                System.out.println("✅ Superadmin created with username=superadmin, password=Super@123");
            } else {
                System.out.println("ℹ️ Superadmin already exists.");
            }
        };
    }

    private Role createRole(String name) {
        Role r = new Role();
        r.setName(name);
        return r;
    }
}
