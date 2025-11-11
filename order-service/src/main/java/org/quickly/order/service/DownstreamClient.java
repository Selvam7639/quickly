package org.quickly.order.service;

import org.quickly.order.model.PaymentRequest;
import org.quickly.order.model.PaymentResult;
import org.quickly.order.model.ReserveRequest;
import org.quickly.order.model.ReserveResult;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

@RequiredArgsConstructor
@Component
public class DownstreamClient {

    private final RestTemplate rt;

    @Retryable(retryFor = { ResourceAccessException.class, RestClientException.class }, maxAttempts = 3)
    public ReserveResult reserve(ReserveRequest req) {
        return rt.postForEntity("http://inventory-service/inventory/reserve", req, ReserveResult.class)
                .getBody();
    }

    @Retryable(retryFor = { ResourceAccessException.class, RestClientException.class }, maxAttempts = 3)
    public PaymentResult pay(PaymentRequest req) {
        return rt.postForEntity("http://payment-service/payment/pay", req, PaymentResult.class)
                .getBody();
    }

    @Recover
    public ReserveResult reserveFallback(Throwable t, ReserveRequest req) {
        return new ReserveResult(req.getProductId(), 0, "DOWNSTREAM_UNAVAILABLE");
    }

    @Recover
    public PaymentResult payFallback(Throwable t, PaymentRequest req) {
        return new PaymentResult(req.getOrderId(), "FAILED", null);
    }
}
