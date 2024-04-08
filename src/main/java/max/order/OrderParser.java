package max.order;

public class OrderParser {
    
    public static Order to(OrderIn in) {
        return Order.builder()
            .idClient(in.idClient())
            .address(in.address())
            .products(in.products())
            .build();
    }

    public static OrderOut to(Order in) {
        return OrderOut.builder()
            .orderId(in.id())
            .clientId(in.idClient())
            .address(in.address())
            .orderDate(in.orderDate())
            .products(in.products())
            .orderValue(in.orderValue())
            .build();
    }
}
