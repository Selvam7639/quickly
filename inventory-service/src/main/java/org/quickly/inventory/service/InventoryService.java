package org.quickly.inventory.service;

import org.quickly.inventory.model.StockDto;
import org.quickly.inventory.model.ReserveRequest;
import org.quickly.inventory.model.ReserveResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InventoryService {
    // productId -> available
    private final Map<UUID, Integer> store = new ConcurrentHashMap<>();

    public InventoryService() {
        // seed one SKU with stock for quick tests
        UUID sample = UUID.randomUUID(); // you'll pass a real productId later
        store.put(sample, 100);
    }

    public List<StockDto> all() {
        return store.entrySet().stream()
                .map(e -> new StockDto(e.getKey(), e.getValue()))
                .toList();
    }

    public StockDto upsert(StockDto stock) {
        store.put(stock.productId(), stock.available());
        return stock;
    }

    public ReserveResult reserve(ReserveRequest req) {
        Integer available = store.getOrDefault(req.productId(), 0);
        if (available < req.quantity()) {
            return new ReserveResult(req.productId(), 0, "INSUFFICIENT");
        }
        store.put(req.productId(), available - req.quantity());
        return new ReserveResult(req.productId(), req.quantity(), "OK");
    }
}
