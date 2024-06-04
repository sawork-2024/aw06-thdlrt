package orders.db;

import models.model.Item;
import models.model.Order;
import models.model.Product;

import java.util.List;

public interface OrderDB {

    Product getProduct(Long productId);

    Order saveOrder();

    Order getOrder(int orderId);

    void changeItem(Long productId, int amount);

    Item getItem(Long productId);

    List<Item>getCart();
}
