CREATE TABLE order
(
    id_order character varying(36) NOT NULL,
    id_client character varying(36) NOT NULL,
    tx_address character varying(256) NOT NULL,
    dt_order date NOT NULL,
    nr_order_value DECIMAL(10, 2) NOT NULL,
    CONSTRAINT order_pkey PRIMARY KEY (id_order)
);