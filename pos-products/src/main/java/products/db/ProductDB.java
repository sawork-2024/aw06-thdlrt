package products.db;

import models.model.Product;

import java.util.List;

public interface ProductDB {

    List<Product> getProducts();

    Product getProduct(Long productId);

    List<Product> searchProductByName(String name);
}
