package poly.petshop.controller.client;

import java.io.IOException;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import poly.petshop.domain.Cart;
import poly.petshop.domain.Product;
import poly.petshop.domain.User;
import poly.petshop.repository.CartRepository;
import poly.petshop.service.ProductService;
import poly.petshop.service.UserService;

@Controller
public class HomePageController {

    private final ProductService productService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final CartRepository cartRepository;

    public HomePageController(ProductService productService,
            PasswordEncoder passwordEncoder,
            UserService userService,
            CartRepository cartRepositor) {
        this.productService = productService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.cartRepository = cartRepositor;
    }

    @GetMapping("/")
    public String getHomePage(Model model, HttpServletRequest req) {
        List<Product> products = this.productService.getAllProducts();
        model.addAttribute("products", products);
        return "client/homepage/index";
    }

    @GetMapping("/shop")
    public String getShoppingPage(Model model) {
        List<Product> products = this.productService.getAllProducts();
        model.addAttribute("products", products);
        return "client/pagechualamcontroller/shop";
    }

    @GetMapping("/myaccount")
    public String getMyAccountPage(Model model) {
        return "client/homepage/my-account";
    }

    @GetMapping("/cart")
    public String getMyCartPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        User user = this.userService.getUserByEmail(email);

        // Lấy danh sách sản phẩm trong giỏ hàng
        List<Cart> cartItems = cartRepository.findByUser(user);
        double totalPrice = 0;
        for (Cart ci : cartItems) {
            totalPrice += ci.getProduct().getGia() * ci.getSoLuongTrongGio();
        }
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "client/cart/cart";
    }

    @PostMapping("/add-product-to-cart/{id}")
    public String postProductToMyCartPage(@PathVariable("id") int productId, HttpServletRequest request, Model model) {

        HttpSession session = request.getSession(false);

        String email = (String) session.getAttribute("email");

        int soLuongTrongGio = 1;

        // Thêm sản phẩm vào giỏ hàng
        this.productService.handleCheckAndAddProductInCart(email, productId, soLuongTrongGio);

        // Cập nhật totalQuantityInCart vào session
        User user = this.userService.getUserByEmail(email);
        session.setAttribute("totalQuantityInCart", user.getTotalQuantityInCart());

        return "redirect:/cart";
    }

    @GetMapping("/thanhtoan")
    public String getMythanhtoanPage(Model model) {
        return "client/pagechualamcontroller/thanhtoan";
    }

    @GetMapping("/404")
    public String getMy404Page(Model model) {
        return "client/pagechualamcontroller/404";
    }

    @GetMapping("/contact")
    public String getMycontactPage(Model model) {
        return "client/pagechualamcontroller/contact";
    }

    @GetMapping("/danhgia")
    public String getMydanhgiaPage(Model model) {
        return "client/pagechualamcontroller/danhgia";
    }

    @GetMapping("/login")
    public String getMydangnhapPage(Model model) {
        return "client/auth/dangnhap";
    }

    // k can lam vi springboot xu ly
    // @PostMapping("/logout")
    // public String performLogout(Model model) {
    // // .. perform logout
    // return "redirect:/";
    // }

    @GetMapping("/client/auth/dangky")
    public String getMydangkyPage(Model model, @ModelAttribute("user") User user) {
        return "client/auth/dangky";
    }

    @PostMapping("/client/auth/dangky")
    public String getPostMydangkyPage(@ModelAttribute("user") @Valid User user,
            BindingResult newUserBindingResult, Model model) throws IOException {
        // validate
        List<FieldError> errors = newUserBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(error.getField() + " - " + error.getDefaultMessage());
        }
        if (newUserBindingResult.hasErrors()) {
            return "/client/auth/dangky";
        }
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("error", "Email đã tồn tại! Vui lòng chọn email khác.");
            return "/client/auth/dangky";
        }
        String hashPass = this.passwordEncoder.encode(user.getMatKhau());
        user.setMatKhau(hashPass);
        user.setUserRole("User");
        this.userService.handleSaveUser(user);
        return "redirect:/client/auth/dangnhap";
    }

    @GetMapping("/client/auth/quenmatkhau")
    public String getMyquenmatkhauPage(Model model) {
        return "client/auth/quenmatkhau";
    }

    @GetMapping("/resetpass")
    public String getMyresetpassPage(Model model) {
        return "client/pagechualamcontroller/resetpass";
    }
}
