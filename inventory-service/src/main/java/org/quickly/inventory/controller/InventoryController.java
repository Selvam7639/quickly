package org.quickly.inventory.web;

import jakarta.validation.Valid;
import org.quickly.inventory.model.StockDto;
import org.quickly.inventory.model.ReserveRequest;
import org.quickly.inventory.model.ReserveResult;
import org.quickly.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) { this.service = service; }

    @GetMapping("/stock")
    public List<StockDto> all() {
        return service.all();
    }

    @PostMapping("/stock")
    public StockDto upsert(@Valid @RequestBody StockDto stock) {
        return service.upsert(stock);
    }

    @PostMapping("/reserve")
    public ResponseEntity<ReserveResult> reserve(@Valid @RequestBody ReserveRequest req) {
        ReserveResult result = service.reserve(req);
        if (result.reserved() == 0) return ResponseEntity.badRequest().body(result);
        return ResponseEntity.ok(result);
    }
}
