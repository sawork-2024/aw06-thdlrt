package orders.web;

import models.model.Item;
import models.model.Order;
import orders.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Order") // 定义API的基础路径
public class OrderController{
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/add/{productId}")
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public ResponseEntity<Boolean> addProduct(@PathVariable Long productId) {
        if(orderService.getProduct(productId) == null)
            return ResponseEntity.ok(false);
        orderService.addItem(productId);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/delete/{productId}")
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable Long productId) {
        if(orderService.getProduct(productId) == null)
            return ResponseEntity.ok(false);
        orderService.deleteItem(productId);
        return ResponseEntity.ok(true);
    }


    @GetMapping("/checkout")
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public ResponseEntity<Boolean> checkCart() {
        orderService.checkoutOrder();
        return ResponseEntity.ok(true);
    }

    @GetMapping("/order/{orderId}")
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public ResponseEntity<Order> getOrder(@PathVariable Integer orderId) {
        Order theOrder = orderService.getOrder(orderId);
        if (theOrder == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(theOrder);
    }

    @GetMapping("/cart")
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public ResponseEntity<List<Item>> getCart() {
        List<Item> cart = orderService.getCart();
        return ResponseEntity.ok(cart);
    }
}
