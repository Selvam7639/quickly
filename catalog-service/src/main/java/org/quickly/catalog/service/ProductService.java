package org.quickly.catalog.service;

import org.quickly.catalog.entity.Product;
import org.quickly.catalog.mapper.ProductMapper;
import org.quickly.catalog.model.ProductDto;
import org.quickly.catalog.repo.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) { this.repo = repo; }

    @Transactional(readOnly = true)
    public List<ProductDto> all() {
        return repo.findAll().stream().map(ProductMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Optional<ProductDto> byId(UUID id) {
        return repo.findById(id).map(ProductMapper::toDto);
    }

    public ProductDto create(ProductDto request) {
        Product saved = repo.save(ProductMapper.toEntity(request));
        return ProductMapper.toDto(saved);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
