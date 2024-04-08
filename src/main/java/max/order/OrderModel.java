package max.order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "order")
@EqualsAndHashCode(of = "id")
@Builder @Getter @Setter @Accessors(fluent = true, chain = true)
@NoArgsConstructor @AllArgsConstructor
public class OrderModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_order")
    private String id;

    @Column(name = "id_client")
    private String idClient;

    @Column(name = "tx_address")
    private String address;

    @Column(name = "dt_order_date")
    private LocalDate orderDate;

    @Column(name = "nr_order_value")
    private Double orderValue;

    @ElementCollection
    @CollectionTable(name = "order_product_detail", joinColumns = @JoinColumn(name = "id_order"))
    private List<OrderProductDetail> products;

    public OrderModel(Order o) {
        this.id = o.id();
        this.idClient = o.idClient();
        this.address = o.address();
        this.orderDate = LocalDate.now();
        this.orderValue = 0.0;

        this.products = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : o.products().entrySet()) {
            products.add(new OrderProductDetail(entry.getKey(), entry.getValue()));
        }
    }

    public Order to() {
        return Order.builder()
            .id(id)
            .idClient(idClient)
            .address(address)
            .orderDate(orderDate.toString())
            .orderValue(orderValue)
            .products(products.stream().collect(Collectors.toMap(OrderProductDetail::idProduct, OrderProductDetail::quantity)))
            .build();
    }
}

@Embeddable
@Getter @Setter @Accessors(fluent = true, chain = true)
@NoArgsConstructor @AllArgsConstructor
class OrderProductDetail {
    private String idProduct;
    private Integer quantity;
}