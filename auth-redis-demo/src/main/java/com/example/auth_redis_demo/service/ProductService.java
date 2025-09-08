package com.example.auth_redis_demo.service;

import com.example.auth_redis_demo.model.Product;
import com.example.auth_redis_demo.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    // Get all products
    @Cacheable(value = "products_list")
    public List<Product> getAll() {
        System.out.println("DB call: getAll products");
        return repo.findAll();
    }

    // Get single product by ID
    @Cacheable(value = "product", key = "#id")
    public Product getById(Long id) {
        System.out.println("DB call: getById " + id);
        return repo.findById(id).orElse(null);
    }

    // Create new product
    @CacheEvict(value = "products_list", allEntries = true)
    @CachePut(value = "product", key = "#result.id")
    public Product create(Product p) {
        return repo.save(p);
    }

    // Update existing product
    @CacheEvict(value = "products_list", allEntries = true)
    @CachePut(value = "product", key = "#result.id")
    public Product update(Long id, Product p) {
        return repo.findById(id).map(existing -> {
            existing.setName(p.getName());
            existing.setPrice(p.getPrice());
            existing.setQuantity(p.getQuantity());
            existing.setDescription(p.getDescription());
            return repo.save(existing);
        }).orElse(null);
    }

    // Delete product
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id"),
            @CacheEvict(value = "products_list", allEntries = true)
    })
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
