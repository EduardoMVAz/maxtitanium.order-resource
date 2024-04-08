package max.order;

import java.util.stream.Collectors;

public class OrderParser {
    
    public static Order to(OrderIn in) {
        return Order.builder()
            .idClient(in.idClient())
            .address(in.address())
            .products(in.products().stream().map(OrderParser::to).collect(Collectors.toList()))
            .build();
    }

    public static OrderOut to(Order in) {
        return OrderOut.builder()
            .orderId(in.id())
            .clientId(in.idClient())
            .address(in.address())
            .orderDate(in.orderDate())
            .products(in.products().stream().map(OrderParser::to).collect(Collectors.toList()))
            .orderValue(in.orderValue())
            .build();
    }

    public static OrderDetail to(OrderDetailIn in) {
        return OrderDetail.builder()
            .idProduct(in.idProduct())
            .quantity(in.quantity())
            .build();
    }

    public static OrderDetailOut to(OrderDetail in) {
        return OrderDetailOut.builder()
            .idProduct(in.idProduct())
            .quantity(in.quantity())
            .build();
    }
}
