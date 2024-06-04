package orders.db;

import models.model.Product;
import models.model.Order;
import models.model.Item;
import orders.client.ProductClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class OrderDBImp implements OrderDB {
    private final List<Order> orders = new ArrayList<>();
    private final List<Item> cart = new ArrayList<>();
    int orderId = 0;
    @Autowired
    private ProductClient productClient;

    @Override
    public Product getProduct(Long productId) {
        return productClient.getProductById(productId);
    }

    @Override
    public Order saveOrder() {
        orderId++;
        Order newOrder = new Order(orderId, List.copyOf(cart));
        orders.add(newOrder);
        cart.clear();
        return newOrder;
    }

    @Override
    public Order getOrder(int orderId) {
        for (Order order : orders)
            if (order.getOrderId() == orderId)
                return order;
        return null;
    }

    @Override
    public void changeItem(Long productId, int deltaAmount) {
        for (Item item : cart) {
            if (Objects.equals(item.getProduct().getId(), productId)) {
                item.setAmount(item.getAmount() + deltaAmount);
                if (item.getAmount() == 0)
                    cart.remove(item);
                return;
            }
        }
        if (deltaAmount > 0)
            cart.add(new Item(getProduct(productId), deltaAmount));
    }

    @Override
    public Item getItem(Long productId) {
        for (Item item : cart) {
            if (Objects.equals(item.getProduct().getId(), productId))
                return item;
        }
        return null;
    }

    @Override
    public List<Item> getCart() {
        return cart;
    }
}
