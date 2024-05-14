package products.service;

import products.model.Product;

import java.util.List;

public interface IProductService {
    List<Product> products();

    Product getProduct(String productId);

    Product setProductQuantity(String productId, int quantity);
}
