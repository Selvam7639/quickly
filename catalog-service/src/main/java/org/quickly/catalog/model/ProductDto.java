package org.quickly.catalog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(
        UUID id,
        @NotBlank String sku,
        @NotBlank String name,
        @Positive BigDecimal price,
        String description
) {}
