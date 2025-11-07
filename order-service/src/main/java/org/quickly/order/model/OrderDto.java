package org.quickly.order.model;

import java.util.UUID;

public record OrderDto(
        UUID id,
        UUID productId,
        int quantity,
        String status // NEW, RESERVED, PAID, CONFIRMED, FAILED
) {}
