package max.order;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderResource implements OrderController {
    
    @Autowired
    private OrderService orderService;

    @GetMapping("/orders/info")
	public ResponseEntity<Map<String, String>> info() {
        return new ResponseEntity<Map<String, String>>(
            Map.ofEntries(
                Map.entry("microservice.name", OrderApplication.class.getSimpleName()),
                Map.entry("os.arch", System.getProperty("os.arch")),
                Map.entry("os.name", System.getProperty("os.name")),
                Map.entry("os.version", System.getProperty("os.version")),
                Map.entry("file.separator", System.getProperty("file.separator")),
                Map.entry("java.class.path", System.getProperty("java.class.path")),
                Map.entry("java.home", System.getProperty("java.home")),
                Map.entry("java.vendor", System.getProperty("java.vendor")),
                Map.entry("java.vendor.url", System.getProperty("java.vendor.url")),
                Map.entry("java.version", System.getProperty("java.version")),
                Map.entry("line.separator", System.getProperty("line.separator")),
                Map.entry("path.separator", System.getProperty("path.separator")),
                Map.entry("user.dir", System.getProperty("user.dir")),
                Map.entry("user.home", System.getProperty("user.home")),
                Map.entry("user.name", System.getProperty("user.name")),
                Map.entry("jar", new java.io.File(
                    OrderApplication.class.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath()
                    ).toString())
            ), HttpStatus.OK
        );
	}

    @Override
    public ResponseEntity<OrderOut> read(String id) {

        final Order dbOrder = orderService.read(id);

        if (dbOrder == null) {
            throw new RuntimeException("Order id not found");
        }

        return new ResponseEntity<OrderOut>(
            OrderParser.to(dbOrder),
            HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<List<OrderOut>> readByClient(String clientId) {
        final List<Order> dbOrders = orderService.readByClient(clientId);

        if (dbOrders == null) {
            throw new RuntimeException("Client id not found");
        }

        return new ResponseEntity<List<OrderOut>>(
            dbOrders.stream()
                .map(o -> OrderParser.to(o))
                .collect(Collectors.toList()),
            HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<OrderOut> create(OrderIn orderIn) {
        final Order order = OrderParser.to(orderIn);
        // Acessar outro microsserviço e setar o valor do pedido

        final Order dbOrder = orderService.create(order);

        if (dbOrder == null) {
            throw new RuntimeException("Order must have products");
        }

        return new ResponseEntity<OrderOut>(
            OrderParser.to(dbOrder),
            HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<OrderOut> update(String id, OrderIn orderIn) {
        final Order order = OrderParser.to(orderIn);

        final Order dbOrder = orderService.update(id, order);

        return new ResponseEntity<OrderOut>(
            OrderParser.to(dbOrder),
            HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<OrderOut> delete(String id) {
        final Order dbOrder = orderService.delete(id);

        return new ResponseEntity<OrderOut>(
            OrderParser.to(dbOrder),
            HttpStatus.OK
        );
    }
}
