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
@Table(name = "order_detail")
@EqualsAndHashCode(of = "id")
@Builder @Getter @Setter @Accessors(fluent = true, chain = true)
@NoArgsConstructor @AllArgsConstructor
public class OrderDetailModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_order_detail")
    private String id;

    @Column(name = "id_order")
    private String idOrder;

    @Column(name = "id_product")
    private String idProduct;

    @Column(name = "nr_quantity")
    private Integer quantity;

    public OrderDetailModel(OrderDetail o) {
        this.idOrder = o.idOrder();
        this.idProduct = o.idProduct();
        this.quantity = o.quantity();
    }

    public OrderDetail to() {
        return OrderDetail.builder()
            .id(id)
            .idOrder(idOrder)
            .idProduct(idProduct)
            .quantity(quantity)
            .build();
    }
}
