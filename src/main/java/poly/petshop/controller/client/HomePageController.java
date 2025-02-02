package poly.petshop.controller.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import poly.petshop.domain.Product;
import poly.petshop.domain.User;
import poly.petshop.service.ProductService;
import poly.petshop.service.UserService;

@Controller
public class HomePageController {

    private final ProductService productService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public HomePageController(ProductService productService,
            PasswordEncoder passwordEncoder,
            UserService userService) {
        this.productService = productService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @GetMapping("/")
    public String getHomePage(Model model, HttpServletRequest req) {
        List<Product> products = this.productService.getAllProducts();
        model.addAttribute("products", products);
        return "client/homepage/show";
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
    public String getMyCartPage(Model model, @ModelAttribute("product") Product product) {
        model.addAttribute("products", productService.getAllProducts());
        return "client/pagechualamcontroller/cart";
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

    // login Google
    @GetMapping("/oauth2/authorization/google")
    public String googleLogin() {
        return "redirect:/oauth2/authorization/google";
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
