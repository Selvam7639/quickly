-- orders table
CREATE TABLE IF NOT EXISTS "order" (
                                       id UUID PRIMARY KEY,
                                       product_id UUID NOT NULL,
                                       quantity INT NOT NULL,
                                       status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_order_product_id ON "order"(product_id);

-- order_item (optional)
CREATE TABLE IF NOT EXISTS order_item (
                                          id UUID PRIMARY KEY,
                                          order_id UUID NOT NULL REFERENCES "order"(id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    quantity INT NOT NULL,
    unit_price numeric(12,2) NOT NULL
    );

-- idempotency keys
CREATE TABLE IF NOT EXISTS idempotency_key (
                                               key_value VARCHAR(128) PRIMARY KEY,
    order_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_idempotency_order ON idempotency_key(order_id);

-- outbox table (simple JSON payload)
CREATE TABLE IF NOT EXISTS outbox (
                                      id UUID PRIMARY KEY,
                                      aggregate_type VARCHAR(100),
    aggregate_id UUID,
    type VARCHAR(100),
    payload text NOT NULL,
    state VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    processed_at TIMESTAMP WITH TIME ZONE
                             );

CREATE INDEX IF NOT EXISTS idx_outbox_state ON outbox(state);
CREATE INDEX IF NOT EXISTS idx_outbox_aggregate ON outbox(aggregate_type, aggregate_id);
