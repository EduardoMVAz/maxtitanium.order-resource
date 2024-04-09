package max.order;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import max.order.exceptions.ExceptionOut;
import max.order.exceptions.OrderNotFoundException;
import max.order.exceptions.ProductNotFoundException;
import max.order.exceptions.ProductsRequiredException;

@ControllerAdvice
public class OrderHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<ExceptionOut> handleOrderNotFoundException(OrderNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionOut(HttpStatus.NOT_FOUND, e.getMessage(), LocalDateTime.now().toString()));
    }

    @ExceptionHandler(ProductsRequiredException.class)
    ResponseEntity<ExceptionOut> handleProductsRequiredException(ProductsRequiredException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionOut(HttpStatus.BAD_REQUEST, e.getMessage(), LocalDateTime.now().toString()));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    ResponseEntity<ExceptionOut> handleProductNotFoundException(ProductNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionOut(HttpStatus.NOT_FOUND, e.getMessage(), LocalDateTime.now().toString()));
    }

}
