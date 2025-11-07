package org.quickly.order.model;

import java.util.UUID;

public record ReserveRequest(UUID productId, int quantity) {}
