package org.quickly.order.service;

import lombok.RequiredArgsConstructor;
import org.quickly.order.entity.IdempotencyKeyEntity;
import org.quickly.order.entity.OrderEntity;
import org.quickly.order.entity.OutboxEntity;
import org.quickly.order.model.CreateOrderRequest;
import org.quickly.order.model.OrderDto;
import org.quickly.order.model.PaymentRequest;
import org.quickly.order.model.PaymentResult;
import org.quickly.order.model.ReserveRequest;
import org.quickly.order.model.ReserveResult;
import org.quickly.order.repo.IdempotencyRepository;
import org.quickly.order.repo.OrderRepository;
import org.quickly.order.repo.OutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final IdempotencyRepository idemRepo;
    private final OutboxRepository outboxRepo;
    private final DownstreamClient downstream;

    @Transactional
    public OrderDto create(String idemKey, CreateOrderRequest req) {
        // idempotency check
        if (idemKey != null) {
            Optional<IdempotencyKeyEntity> prior = idemRepo.findById(idemKey);
            if (prior.isPresent() && prior.get().getOrderId() != null) {
                OrderEntity existing = orderRepo.findById(prior.get().getOrderId()).orElseThrow();
                return toDto(existing);
            }
        }

        UUID id = UUID.randomUUID();
        OrderEntity order = OrderEntity.builder()
                .id(id)
                .productId(req.getProductId())
                .quantity(req.getQuantity())
                .status("NEW")
                .build();
        orderRepo.save(order);

        if (idemKey != null) {
            idemRepo.save(IdempotencyKeyEntity.builder().keyValue(idemKey).orderId(id).build());
        }

        // reserve
        ReserveResult rr = downstream.reserve(new ReserveRequest(req.getProductId(), req.getQuantity()));
        if (rr == null || rr.getReserved() == 0) {
            order.setStatus("FAILED");
            orderRepo.save(order);
            return toDto(order);
        }
        order.setStatus("RESERVED");
        orderRepo.save(order);

        // payment (fixed price demo)
        BigDecimal amount = new BigDecimal("10.00").multiply(BigDecimal.valueOf(req.getQuantity()));
        PaymentResult pr = downstream.pay(new PaymentRequest(id, amount));
        if (pr == null || !"SUCCESS".equalsIgnoreCase(pr.getStatus())) {
            order.setStatus("FAILED");
            orderRepo.save(order);
            return toDto(order);
        }

        order.setStatus("CONFIRMED");
        orderRepo.save(order);

        // outbox
        String payload = "{\"orderId\":\"" + id + "\",\"status\":\"CONFIRMED\"}";
        outboxRepo.save(OutboxEntity.builder()
                .id(UUID.randomUUID())
                .aggregateType("order")
                .aggregateId(id)
                .type("OrderConfirmed")
                .payload(payload)
                .build());

        return toDto(order);
    }

    private OrderDto toDto(OrderEntity e) {
        return OrderDto.builder()
                .id(e.getId())
                .productId(e.getProductId())
                .quantity(e.getQuantity())
                .status(e.getStatus())
                .build();
    }



    @Transactional(readOnly = true)
    public Optional<OrderDto> getById(java.util.UUID id) {
        return orderRepo.findById(id).map(this::toDto);
    }

}
