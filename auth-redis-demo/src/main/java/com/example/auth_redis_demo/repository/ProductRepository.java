package com.example.auth_redis_demo.repository;

import com.example.auth_redis_demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
