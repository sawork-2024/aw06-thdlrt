package products.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import products.db.PosDB;
import products.model.Product;

import java.util.List;

@Service
public class PosService implements IProductService {

    private PosDB posDB;

    @Autowired
    public void setPosDB(PosDB posDB) {
        this.posDB = posDB;
    }

    @Override
    public List<Product> products() {
        return posDB.getProducts();
    }

    @Override
    public Product getProduct(String productId) {
        Product p = posDB.getProduct(productId);
        if(p == null)
            throw new HttpClientErrorException(HttpStatusCode.valueOf(404));
        return p;
    }

    @Override
    public Product setProductQuantity(String productId, int quantity) {
        return posDB.setProductQuantity(productId, quantity);
    }
}
