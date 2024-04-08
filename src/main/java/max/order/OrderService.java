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
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductController productController;

    @CachePut(value = "orders", key = "#result.id()")
    public Order create(Order in) {
        OrderModel order = new OrderModel(in);

        if (in.products() != null) {
            for (OrderDetail p : in.products()) {
                final String idProduct = p.idProduct();
                final Integer quantity = p.quantity();

                final ProductOut product = productController.read(idProduct).getBody();
                if (product == null) {
                    throw new RuntimeException("Product id not found");
                }

                order.orderValue(order.orderValue() + product.price() * quantity);
                p.idOrder(order.id());
                // todo: tirar o save do for
                orderDetailRepository.save(new OrderDetailModel(p));
            }
        } else {
            return null;
        }

        Order savedOrder = orderRepository.save(order).to();
        savedOrder.products(in.products());

        return savedOrder;
    }

    @Cacheable(value = "orders", key = "#id", unless = "#result == null")
    public Order read(String id) {
        Order savedOrder = orderRepository.findById(id).orElse(null).to();

        if (savedOrder == null) {
            return null;
        }

        List<OrderDetail> products = orderDetailRepository.findByIdOrder(id)
            .stream()
            .map(OrderDetailModel::to)
            .collect(Collectors.toList());

        savedOrder.products(products);

        return savedOrder; 
    }

    public List<Order> readByClient(String idClient) {
        List<Order> savedOrder = orderRepository.findByIdClient(idClient)
            .stream()
            .map(OrderModel::to)
            .collect(Collectors.toList());
        
        if (savedOrder == null) {
            return null;
        }

        for (Order o : savedOrder) {
            List<OrderDetail> products = orderDetailRepository.findByIdOrder(o.id())
                .stream()
                .map(OrderDetailModel::to)
                .collect(Collectors.toList());

            o.products(products);
        }

        return savedOrder;
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
            orderDetailRepository.deleteByIdOrder(id);
            dbOrder.orderValue(0.0);

            for (OrderDetail p : in.products()) {
                final String idProduct = p.idProduct();
                final Integer quantity = p.quantity();

                final ProductOut product = productController.read(idProduct).getBody();
                if (product == null) {
                    throw new RuntimeException("Product id not found");
                }

                dbOrder.orderValue(dbOrder.orderValue() + product.price() * quantity);
                p.idOrder(dbOrder.id());
                // todo: tirar o save do for
                orderDetailRepository.save(new OrderDetailModel(p));
            }
        }
        
        Order savedOrder = orderRepository.save(dbOrder).to();
        savedOrder.products(in.products());

        return savedOrder;
    }

    @CachePut(value = "orders", key = "#id")
    public Order delete(String id) {

        if (!orderRepository.existsById(id)) {
            return null;
        }

        final OrderModel dbOrder = orderRepository.findById(id).orElse(null);
        orderRepository.deleteById(id);
        
        final List<OrderDetail> dbOrderDetail = orderDetailRepository.findByIdOrder(id)
            .stream()
            .map(OrderDetailModel::to)
            .collect(Collectors.toList());

        orderDetailRepository.deleteByIdOrder(id);

        Order order = dbOrder.to();
        order.products(dbOrderDetail);

        return order;
    }
}
