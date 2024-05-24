package max.order;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import feign.FeignException;
import max.order.exceptions.OrderNotFoundException;
import max.order.exceptions.ProductNotFoundException;
import max.order.exceptions.ProductsRequiredException;
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
        order = orderRepository.save(order);

        if (in.products() != null) {
            for (OrderDetail p : in.products()) {
                final String idProduct = p.idProduct();
                final Integer quantity = p.quantity();

                try {
                    final ProductOut product = productController.read(idProduct).getBody();
                    if (product == null) {
                        orderRepository.deleteById(order.id());
                        throw new ProductNotFoundException(idProduct);
                    }
    
                    order.orderValue(order.orderValue() + product.price() * quantity);
                    p.idOrder(order.id());
                    // todo: tirar o save do for
                    orderDetailRepository.save(new OrderDetailModel(p));

                } catch (FeignException e) {
                    orderRepository.deleteById(order.id());
                    throw new ProductNotFoundException(idProduct);
                }
            }
        } else {
            orderRepository.deleteById(order.id());
            throw new ProductsRequiredException("Products are required to create an order");
        }

        Order savedOrder = orderRepository.save(order).to();
        savedOrder.products(in.products());

        return savedOrder;
    }

    @Cacheable(value = "orders", key = "#id", unless = "#result == null")
    public Order read(String id) {
        OrderModel orderDB = orderRepository.findById(id).orElse(null);

        if (orderDB == null) {
            throw new OrderNotFoundException(id);
        }

        Order savedOrder = orderDB.to();

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
            throw new OrderNotFoundException(id);
        }

        if (in.idClient() != null) {
            dbOrder.idClient(in.idClient());
        }

        if (in.address() != null) {
            dbOrder.address(in.address());
        }

        if (in.products() != null) {
            List<OrderDetailModel> oldDetails = orderDetailRepository.findByIdOrder(id);
            dbOrder.orderValue(0.0);

            for (OrderDetail p : in.products()) {
                final String idProduct = p.idProduct();
                final Integer quantity = p.quantity();
                
                try {
                    final ProductOut product = productController.read(idProduct).getBody();
                    if (product == null) {
                        throw new ProductNotFoundException(idProduct);
                    }

                    dbOrder.orderValue(dbOrder.orderValue() + product.price() * quantity);
                    p.idOrder(dbOrder.id());
                    // todo: tirar o save do for
                    orderDetailRepository.save(new OrderDetailModel(p));

                } catch (FeignException e){
                    throw new ProductNotFoundException(idProduct);
                }
            }

            // delete old details
            for (OrderDetailModel od : oldDetails) {
                orderDetailRepository.deleteById(od.id());
            } 
        }
        
        Order savedOrder = orderRepository.save(dbOrder).to();
        savedOrder.products(in.products());

        return savedOrder;
    }

    @CachePut(value = "orders", key = "#id")
    public Order delete(String id) {

        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(id);
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

    public List<Order> list() {
        List<OrderModel> savedOrderModels = new ArrayList<>();
        orderRepository.findAll().forEach(savedOrderModels::add);

        List<Order> savedOrders = savedOrderModels.stream()
            .map(OrderModel::to)
            .collect(Collectors.toList());

        if (savedOrders.isEmpty()) {
            return null;
        }

        for (Order o : savedOrders) {
            List<OrderDetail> products = orderDetailRepository.findByIdOrder(o.id())
                .stream()
                .map(OrderDetailModel::to)
                .collect(Collectors.toList());
            o.products(products);
        }

        return savedOrders;
    }

}
