package org.quickly.inventory.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StockDto(
        @NotNull UUID productId,
        @Min(0) int available
) {}
