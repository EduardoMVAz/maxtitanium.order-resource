package max.order;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import max.product.ProductController;
import max.product.ProductOut;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductController productController;

    @CachePut(value = "orders", key = "#result.id()")
    public Order create(Order in) {
        OrderModel order = new OrderModel(in);

        if (order.products() != null) {
            order.products().forEach(p -> {
                final String idProduct = p.idProduct();
                final Integer quantity = p.quantity();

                final ProductOut product = productController.read(idProduct).getBody();
                if (product == null) {
                    throw new RuntimeException("Product id not found");
                }

                order.orderValue(order.orderValue() + product.price() * quantity);
            });
        } else {
            return null;
        }

        return orderRepository.save(new OrderModel(in)).to();
    }

    @Cacheable(value = "orders", key = "#id", unless = "#result == null")
    public Order read(String id) {
        return orderRepository.findById(id).map(OrderModel::to).orElse(null);
    }

    public List<Order> readByClient(String idClient) {
        return orderRepository.findByIdClient(idClient).stream()
            .map(OrderModel::to)
            .collect(Collectors.toList());
    }

    @CachePut(value = "orders", key = "#result.id()", unless = "#result == null")
    public Order update(String id, Order in) {
        OrderModel dbOrder = orderRepository.findById(id).orElse(null);

        if (dbOrder == null) {
            return null;
        }

        if (in.idClient() != null) {
            dbOrder.idClient(in.idClient());
        }

        if (in.address() != null) {
            dbOrder.address(in.address());
        }

        if (in.products() != null) {
            dbOrder.products(in.products().entrySet().stream()
                .map(e -> new OrderProductDetail(e.getKey(), e.getValue()))
                .collect(Collectors.toList()));
            
            for (OrderProductDetail p : dbOrder.products()) {
                final String idProduct = p.idProduct();
                final Integer quantity = p.quantity();

                final ProductOut product = productController.read(idProduct).getBody();
                if (product == null) {
                    throw new RuntimeException("Product id not found");
                }

                dbOrder.orderValue(dbOrder.orderValue() + product.price() * quantity);
            }
        }

        return orderRepository.save(new OrderModel(in)).to();
    }

    @CachePut(value = "orders", key = "#id")
    public Order delete(String id) {
        final OrderModel dbProduct = orderRepository.findById(id).orElse(null);

        if (dbProduct == null) {
            return null;
        }

        orderRepository.deleteById(id);
        return dbProduct.to();
    }
}
