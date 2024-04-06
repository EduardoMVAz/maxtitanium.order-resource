package max.order;

import jakarta.persistence.Column;
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
    private String clientId;

    @Column(name = "tx_address")
    private String address;

    @Column(name = "nr_order_value")
    private Integer orderValue;

    public OrderModel(Order o) {
        this.id = o.id();
        this.clientId = o.clientId();
        this.address = o.address();
        this.orderValue = o.orderValue();
    }

    public Order to() {
        return Order.builder()
            .id(id)
            .clientId(clientId)
            .address(address)
            .orderValue(orderValue)
            .build();
    }
}
