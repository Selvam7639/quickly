package org.quickly.order.controller;

import jakarta.validation.Valid;
import org.quickly.order.model.CreateOrderRequest;
import org.quickly.order.model.OrderDto;
import org.quickly.order.service.IdempotencyService;
import org.quickly.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService service;
    private final IdempotencyService idem;

    public OrderController(OrderService service, IdempotencyService idem) {
        this.service = service; this.idem = idem;
    }


    @GetMapping
    public List<OrderDto> all() { return service.all(); }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> byId(@PathVariable("id") UUID id) {
        return service.byId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrderDto> create(
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
            @Valid @RequestBody CreateOrderRequest req) {

        // if we already processed this key, return the same response
        if (idemKey != null) {
            OrderDto prior = idem.get(idemKey);
            if (prior != null) return ResponseEntity.created(URI.create("/order/" + prior.id())).body(prior);
        }

        OrderDto saved = service.create(req);

        // cache result for idempotency
        if (idemKey != null) idem.put(idemKey, saved);

        return ResponseEntity.created(URI.create("/order/" + saved.id())).body(saved);
    }

}
