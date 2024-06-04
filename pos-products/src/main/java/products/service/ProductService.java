package products.service;

import models.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    Product getProductById(Long productId);

    List<Product> searchProductByName(String name);
}
