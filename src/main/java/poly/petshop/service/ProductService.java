package poly.petshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import poly.petshop.domain.Product;
import poly.petshop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        List<Product> products = this.productRepository.findAll();
        return products != null ? products : List.of(); // Trả về danh sách rỗng nếu `null`
    }

    // public List<Category> getAllUsersByEmail(String email) {
    // return this.userRepository.findByEmail(email);
    // }
    public boolean skuExists(String sku) {
        return productRepository.existsBysku(sku);
    }

    public Product getProductById(int productId) {
        return this.productRepository.findById(productId);
    }

    public Product handleSaveProduct(Product product) {
        Product pro = this.productRepository.save(product);
        System.out.println(pro);
        return pro;
    }

    public void deletetProductById(int productId) {
        this.productRepository.deleteById(productId);
    }
}
