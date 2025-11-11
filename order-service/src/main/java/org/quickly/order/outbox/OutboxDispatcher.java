package org.quickly.order.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quickly.order.entity.OutboxEntity;
import org.quickly.order.repo.OutboxRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class OutboxDispatcher {

    private final OutboxRepository outboxRepository;

    /**
     * Simple scheduled dispatcher:
     * - runs every 5 seconds (adjust for dev)
     * - fetches PENDING rows, attempts to publish, marks PROCESSED
     * - this is synchronous and mock; replace publishWithBroker(...) with real publisher
     */
    @Scheduled(fixedDelayString = "${outbox.dispatch.delay.ms:5000}")
    public void dispatchPending() {
        List<OutboxEntity> pending = outboxRepository.findByState("PENDING");
        if (pending.isEmpty()) return;

        log.info("OutboxDispatcher: found {} pending events", pending.size());
        for (OutboxEntity row : pending) {
            try {
                publishAndMark(row);
            } catch (Exception ex) {
                log.warn("Failed to process outbox id={} — will retry later: {}", row.getId(), ex.getMessage());
            }
        }
    }

    @Transactional
    public void publishAndMark(OutboxEntity row) {
        // MOCK publish: replace this with Kafka/RabbitMQ publish logic
        boolean ok = publishWithBroker(row);
        if (ok) {
            row.setState("PROCESSED");
            row.setProcessedAt(Instant.now());
            outboxRepository.save(row); // save state change within same tx
            log.info("OutboxDispatcher: processed id={}", row.getId());
        } else {
            log.warn("OutboxDispatcher: publish failed for id={}", row.getId());
            // keep state PENDING for future retries
        }
    }

    private boolean publishWithBroker(OutboxEntity row) {
        // Mock behavior: just log payload and return true
        log.info("Mock publish: aggregate={} type={} payload={}",
                row.getAggregateType(), row.getType(), row.getPayload());
        return true;
    }
}
