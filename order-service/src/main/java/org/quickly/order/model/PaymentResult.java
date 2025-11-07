package org.quickly.order.model;

import java.util.UUID;

public record PaymentResult(UUID orderId, String status, String txRef) {}
