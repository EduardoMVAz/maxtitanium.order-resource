package max.order;

import java.time.LocalDate;

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
@Table(name = "t_order")
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

    public OrderModel(Order o) {
        this.id = o.id();
        this.idClient = o.idClient();
        this.address = o.address();
        this.orderDate = LocalDate.now();
        this.orderValue = 0.0;
    }

    public Order to() {
        return Order.builder()
            .id(id)
            .idClient(idClient)
            .address(address)
            .orderDate(orderDate.toString())
            .orderValue(orderValue)
            .build();
    }
}