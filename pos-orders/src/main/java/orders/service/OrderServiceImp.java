package orders.service;

import models.model.Item;
import orders.db.OrderDB;
import models.model.Order;
import models.model.Product;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImp implements OrderService {
    private OrderDB orderDB;

    @Autowired
    public void setPosDB(OrderDB orderDB) {
        this.orderDB = orderDB;
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    void sendOrder(Order order){
        rabbitTemplate.convertAndSend("orderCreatedQueue", order);
    }

    @Override
    public void checkoutOrder() {
        sendOrder(orderDB.saveOrder());
    }

    @Override
    public void addItem(Long productId) {
        orderDB.changeItem(productId, 1);
    }

    @Override
    public void deleteItem(Long productId) {
        orderDB.changeItem(productId, -1 * orderDB.getItem(productId).getAmount());
    }

    @Override
    public Order getOrder(int orderId) {
        return orderDB.getOrder(orderId);
    }

    @Override
    public Product getProduct(Long productId) {
        return orderDB.getProduct(productId);
    }

    @Override
    public List<Item> getCart() {
        return orderDB.getCart();
    }
}
