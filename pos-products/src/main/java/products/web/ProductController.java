package products.web;

import models.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import products.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/Product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 获取所有产品列表
    @GetMapping
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public ResponseEntity<List<Product>> listProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }

    // 根据ID获取特定产品详细信息
    @GetMapping("/{productId}")
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public ResponseEntity<Product> showProductById(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping("/search/{name}")
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public ResponseEntity<List<Product>> searchProductByName(@PathVariable String name) {
        List<Product> products = productService.searchProductByName(name);
        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }
}
