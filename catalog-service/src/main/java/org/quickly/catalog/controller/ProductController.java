package org.quickly.catalog.controller;

import jakarta.validation.Valid;
import org.quickly.catalog.model.ProductDto;
import org.quickly.catalog.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/catalog")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }

    @GetMapping("/products")
    public List<ProductDto> all() { return service.all(); }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> byId(@PathVariable UUID id) {
        return service.byId(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto request) {
        ProductDto saved = service.create(request);
        return ResponseEntity.created(URI.create("/catalog/products/" + saved.id())).body(saved);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
