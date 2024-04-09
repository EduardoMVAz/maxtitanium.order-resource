package max.order.exceptions;

public class OrderNotFoundException extends RuntimeException{
    
        public OrderNotFoundException(String id) {
            super("Order not found with ID: " + id);
        }

}
