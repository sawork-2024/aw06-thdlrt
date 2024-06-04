package products.db;
import products.client.ProductClient;
import models.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDBImp implements ProductDB {

    @Autowired
    private ProductClient productClient;

    @Override
    public List<Product> getProducts() {
        return productClient.getProducts();
    }

    @Override
    public Product getProduct(Long productId) {
        return productClient.getProductById(productId);
    }

    @Override
    public List<Product> searchProductByName(String name) {
        return productClient.searchProductByName(name);
    }
}

