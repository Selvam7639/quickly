package org.quickly.catalog.mapper;

import org.quickly.catalog.entity.Product;
import org.quickly.catalog.model.ProductDto;

import java.util.UUID;

public final class ProductMapper {
    private ProductMapper() {}

    public static Product toEntity(ProductDto dto) {
        UUID id = dto.id() != null ? dto.id() : UUID.randomUUID();
        return new Product(id, dto.sku(), dto.name(), dto.price(), dto.description());
    }

    public static ProductDto toDto(Product e) {
        return new ProductDto(e.getId(), e.getSku(), e.getName(), e.getPrice(), e.getDescription());
    }
}
