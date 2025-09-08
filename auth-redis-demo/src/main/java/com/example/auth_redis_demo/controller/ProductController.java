package com.example.auth_redis_demo.controller;

import com.example.auth_redis_demo.model.Product;
import com.example.auth_redis_demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService svc;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPERADMIN')")
    public List<Product> all() { return svc.getAll(); }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPERADMIN')")
    public ResponseEntity<Product> one(@PathVariable Long id) {
        var p = svc.getById(id);
        return p == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(p);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public Product create(@RequestBody Product p) { return svc.create(p); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public Product update(@PathVariable Long id, @RequestBody Product p) { return svc.update(id,p); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }
}
