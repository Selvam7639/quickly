package org.quickly.payment.controller;

import jakarta.validation.Valid;
import org.quickly.payment.model.PaymentRequest;
import org.quickly.payment.model.PaymentResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @PostMapping("/pay")
    public PaymentResult pay(@Valid @RequestBody PaymentRequest req) {
        // Mock: always success
        return new PaymentResult(req.orderId(), "SUCCESS", UUID.randomUUID().toString());
    }
}
