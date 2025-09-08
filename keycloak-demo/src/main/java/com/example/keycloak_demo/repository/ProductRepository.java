package com.example.keycloak_demo.repository;

import com.example.keycloak_demo.model.Product;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ProductRepository {

    private final Map<Long, Product> productMap = new HashMap<>();
    private long currentId = 1;

    // Cache the list of products in Redis
    @Cacheable(value = "products")
    public List<Product> findAll() {
        System.out.println("Fetching products from repository...");
        return new ArrayList<>(productMap.values());
    }

    // Evict cache on add or update
    @CacheEvict(value = "products", allEntries = true)
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(currentId++);
        }
        productMap.put(product.getId(), product);
        return product;
    }

    // Evict cache on delete
    @CacheEvict(value = "products", allEntries = true)
    public void deleteById(Long id) {
        productMap.remove(id);
    }

    // Get single product (no cache needed)
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(productMap.get(id));
    }
}
