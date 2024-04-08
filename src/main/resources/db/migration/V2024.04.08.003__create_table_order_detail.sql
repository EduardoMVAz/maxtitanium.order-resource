CREATE TABLE order_detail
(
    id_order_detail character varying(36) NOT NULL,
    id_order character varying(36) NOT NULL,
    id_product character varying(36) NOT NULL,
    nr_quantity INT NOT NULL,
    CONSTRAINT order_detail_pkey PRIMARY KEY (id_order_detail)
);