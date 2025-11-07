package org.quickly.order.service;

import org.quickly.order.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    private final Map<UUID, OrderDto> store = new ConcurrentHashMap<>();
    private final DownstreamClient downstream;

    public OrderService(DownstreamClient downstream) {
        this.downstream = downstream;
    }

    public List<OrderDto> all() {
        return new ArrayList<>(store.values());
    }

    public OrderDto create(CreateOrderRequest req) {
        var id = UUID.randomUUID();
        var order = new OrderDto(id, req.productId(), req.quantity(), "NEW");
        store.put(id, order);

        // 1) Reserve
        var reserveRes = downstream.reserve(new ReserveRequest(req.productId(), req.quantity()));
        if (reserveRes == null || reserveRes.reserved() == 0) {
            var failed = new OrderDto(id, req.productId(), req.quantity(), "FAILED");
            store.put(id, failed);
            return failed;
        }
        var reserved = new OrderDto(id, req.productId(), req.quantity(), "RESERVED");
        store.put(id, reserved);

        // 2) Pay (mock)
        var amount = new BigDecimal("10.00").multiply(BigDecimal.valueOf(req.quantity()));
        var payRes = downstream.pay(new PaymentRequest(id, amount));
        if (payRes == null || !"SUCCESS".equalsIgnoreCase(payRes.status())) {
            var failed = new OrderDto(id, req.productId(), req.quantity(), "FAILED");
            store.put(id, failed);
            return failed;
        }

        var confirmed = new OrderDto(id, req.productId(), req.quantity(), "CONFIRMED");
        store.put(id, confirmed);
        return confirmed;
    }


    public Optional<OrderDto> byId(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
}
