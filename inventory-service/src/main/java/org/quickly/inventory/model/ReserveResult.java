package org.quickly.inventory.model;

import java.util.UUID;

public record ReserveResult(
        UUID productId,
        int reserved,        // actual reserved qty
        String reason        // "OK" or why it failed
) {}
