create table if not exists product (
    id uuid primary key,
    sku varchar(64) not null unique,
    name varchar(200) not null,
    price numeric(12,2) not null,
    description varchar(1000)
    );
