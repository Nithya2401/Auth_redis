package com.example.keycloak_demo.controller;

import com.example.keycloak_demo.model.Product;
import com.example.keycloak_demo.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Get all products (cached in Redis)
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    @Cacheable(value = "products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get product by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Add a product (evicts cache)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "products", allEntries = true)
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productRepository.save(product));
    }

    // Update a product (evicts cache)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "products", allEntries = true)
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setName(product.getName());
                    existing.setPrice(product.getPrice());
                    existing.setQuantity(product.getQuantity()); // ✅ include quantity
                    existing.setDescription(product.getDescription());
                    return ResponseEntity.ok(productRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a product (evicts cache)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "products", allEntries = true)
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
