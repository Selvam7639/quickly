package org.quickly.order.model;

import java.util.UUID;

public record ReserveResult(UUID productId, int reserved, String reason) {}
