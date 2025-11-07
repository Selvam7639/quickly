package org.quickly.payment.model;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(@NotNull UUID orderId, @NotNull BigDecimal amount) {}
