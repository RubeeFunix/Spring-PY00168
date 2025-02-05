package poly.petshop.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import poly.petshop.domain.Cart;
import poly.petshop.domain.Product;
import poly.petshop.domain.User;
import poly.petshop.repository.CartRepository;
import poly.petshop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final UserService userService;

    public ProductService(ProductRepository productRepository,
            CartRepository cartRepository,
            UserService userService) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.userService = userService;
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

    public Optional<Product> getProductById(int productId) {
        return productRepository.findById(productId);
    }

    public Product handleSaveProduct(Product product) {
        Product pro = this.productRepository.save(product);
        System.out.println(pro);
        return pro;
    }

    public void deletetProductById(int productId) {
        this.productRepository.deleteById(productId);
    }

    public void handleCheckAndAddProductInCart(String email, int productId, int soLuongTrongGio) {
        User user = this.userService.getUserByEmail(email);
        Optional<Product> productOpt = this.productRepository.findById(productId);

        // Kiểm tra user có tồn tại không
        if (user == null) {
            throw new RuntimeException("Không tìm thấy user với email: " + email);
        }

        // Kiểm tra sản phẩm có tồn tại không
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId);
        }

        // Tạo product thật
        Product product = productOpt.get();

        // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
        Optional<Cart> existingCartOpt = this.cartRepository.findCartByUserAndProduct(user, product);

        if (existingCartOpt.isPresent()) {
            // Get Cart thiệt
            // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng
            Cart existingCart = existingCartOpt.get();

            // Tăng lên 1 để k phải hiện 2 dòng row
            existingCart.setSoLuongTrongGio(existingCart.getSoLuongTrongGio() + soLuongTrongGio);

            // Tiến hành lưu giỏ
            this.cartRepository.save(existingCart);

            System.out.println("Đã cập nhật số lượng sản phẩm trong giỏ hàng.");
        } else {
            // Nếu chưa có trong giỏ hàng, tạo mới
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setProduct(product);
            newCart.setSoLuongTrongGio(soLuongTrongGio);
            newCart.setNgayThemGio(new Date());
            this.cartRepository.save(newCart);
            System.out.println("Đã thêm sản phẩm mới vào giỏ hàng.");
            user.setTotalQuantityInCart(user.getTotalQuantityInCart() + 1);
        }

    }

}
