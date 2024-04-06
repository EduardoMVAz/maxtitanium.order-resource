package max.order;

public class OrderParser {
    
    public static Order to(OrderIn in) {
        return Order.builder()
            .clientId(in.clientId())
            .address(in.address())
            .products(in.products())
            .orderValue(in.orderValue())
            .build();
    }
}
