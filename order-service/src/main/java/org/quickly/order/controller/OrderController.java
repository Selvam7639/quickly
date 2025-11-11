package org.quickly.order.web;

import lombok.RequiredArgsConstructor;
import org.quickly.order.model.CreateOrderRequest;
import org.quickly.order.model.OrderDto;
import org.quickly.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<OrderDto> create(
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
            @Valid @RequestBody CreateOrderRequest req) {

        OrderDto saved = service.create(idemKey, req);
        return ResponseEntity.created(URI.create("/order/" + saved.getId())).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> byId(@PathVariable("id") java.util.UUID id) {
        return service // you need a method to fetch by id if not present yet
                .getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
