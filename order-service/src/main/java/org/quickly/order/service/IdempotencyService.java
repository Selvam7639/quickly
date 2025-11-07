package org.quickly.order.service;

import org.quickly.order.model.OrderDto;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdempotencyService {

    private final Map<String, OrderDto> results = new ConcurrentHashMap<>();

    public OrderDto get(String key) { return results.get(key); }
    public void put(String key, OrderDto dto) { if (key != null) results.put(key, dto); }
    public boolean has(String key) { return key != null && results.containsKey(key); }
}
