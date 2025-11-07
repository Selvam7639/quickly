package org.quickly.catalog.service;

import org.quickly.catalog.model.ProductDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductService {
    private final Map<UUID, ProductDto> store = new ConcurrentHashMap<>();

    public ProductService() {
        // seed one sample
        var seedId = UUID.randomUUID();
        store.put(seedId, new ProductDto(seedId, "BAN-1", "Banana", new BigDecimal("10.00"), "Fresh bananas"));
    }

    public List<ProductDto> all() {
        return new ArrayList<>(store.values());
    }

    public Optional<ProductDto> byId(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    public ProductDto create(ProductDto request) {
        UUID id = UUID.randomUUID();
        ProductDto saved = new ProductDto(id, request.sku(), request.name(), request.price(), request.description());
        store.put(id, saved);
        return saved;
    }

    public void delete(UUID id) {
        store.remove(id);
    }
}
