package products.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import products.db.ProductDB;
import models.model.Product;

import java.util.List;

@Service
public class PosServiceImp implements ProductService {

    private ProductDB productDB;

    @Autowired
    public void setPosDB(ProductDB productDB) {
        this.productDB = productDB;
    }

    @Override
    public List<Product> getAllProducts() {
        return productDB.getProducts();
    }

    @Override
    public Product getProductById(Long productId) {
        Product p = productDB.getProduct(productId);
        if(p == null)
            throw new HttpClientErrorException(HttpStatusCode.valueOf(404));
        return p;
    }

    @Override
    public List<Product> searchProductByName(String name) {
        return productDB.searchProductByName(name);
    }
}
