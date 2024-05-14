package orders.db;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import orders.model.Item;
import orders.model.Order;
import orders.model.Product;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class OrderDBImp implements OrderDB {
    private final List<Order> orders = new ArrayList<>();
    private final List<Item> cart = new ArrayList<>();
    int orderId = 0;
    private List<Product> products = null;

    @Override
    public List<Product> getProducts() {
        return products;
    }

    @Override
    public Product getProduct(String productId) {
        for (Product p : getProducts()) {
            if (p.getPid().equals(productId))
                return p;
        }
        return null;
    }

    @Override
    public void saveOrder() {
        orderId++;
        Order newOrder = new Order(orderId, cart);
        orders.add(newOrder);
        cart.clear();
    }

    @Override
    public Order getOrder(int orderId) {
        for (Order order : orders)
            if (order.getOrderId() == orderId)
                return order;
        return null;
    }

    @Override
    public void changeItem(String productId, int deltaAmount) {
        for (Item item : cart) {
            if (Objects.equals(item.getProduct().getPid(), productId)) {
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
    public Item getItem(String productId) {
        for (Item item : cart) {
            if (Objects.equals(item.getProduct().getPid(), productId))
                return item;
        }
        return null;
    }
}
