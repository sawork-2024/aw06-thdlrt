package orders.service;

import models.model.Item;
import models.model.Order;
import models.model.Product;

import java.util.List;

public interface OrderService {
    void checkoutOrder();

    void addItem(Long productId);

    void deleteItem(Long productId);

    Order getOrder(int orderId);

    Product getProduct(Long productId);

    List<Item> getCart();
}
