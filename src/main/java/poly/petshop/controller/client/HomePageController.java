package poly.petshop.controller.client;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import poly.petshop.domain.Cart;
import poly.petshop.domain.Order;
import poly.petshop.domain.OrderDetail;
import poly.petshop.domain.Product;
import poly.petshop.domain.User;
import poly.petshop.repository.CartRepository;
import poly.petshop.repository.OrderDetailRepository;
import poly.petshop.repository.OrderRepository;
import poly.petshop.service.MailService;
import poly.petshop.service.ProductService;
import poly.petshop.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
public class HomePageController {

    private final ProductService productService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final MailService mailService;

    public HomePageController(ProductService productService,
            PasswordEncoder passwordEncoder,
            UserService userService,
            CartRepository cartRepositor,
            OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            MailService mailService) {
        this.productService = productService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.cartRepository = cartRepositor;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.mailService = mailService;
    }

    @GetMapping("/")
    public String getHomePage(Model model, HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "") String minPrice,
            @RequestParam(defaultValue = "") String maxPrice) {
        // Phân trang
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());

        // Tất cả sản phẩm
        Page<Product> allProducts;
        if (!minPrice.isEmpty() && !maxPrice.isEmpty()) {
            double min = Double.parseDouble(minPrice);
            double max = Double.parseDouble(maxPrice);
            allProducts = productService.searchByPriceRange(min, max, pageable);
        } else {
            allProducts = productService.getAllProducts(pageable);
        }

        // Sản phẩm bán chạy (top 8)
        List<Product> bestSelling = productService.getBestSellingProducts(8);

        // Sản phẩm giảm giá (top 8)
        List<Product> discounted = productService.getDiscountedProducts(8);

        // Sản phẩm mới (top 8)
        List<Product> newProducts = productService.getNewProducts(8);

        // Truyền dữ liệu vào model
        model.addAttribute("allProducts", allProducts.getContent());
        model.addAttribute("bestSelling", bestSelling);
        model.addAttribute("discounted", discounted);
        model.addAttribute("newProducts", newProducts);
        model.addAttribute("currentPage", allProducts.getNumber());
        model.addAttribute("totalPages", allProducts.getTotalPages());
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "client/homepage/index";
    }

    @GetMapping("/shop")
    public String getShoppingPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "") String keyword,
            Model model) {
        // Debug để kiểm tra giá trị nhận được
        System.out.println("SortBy: " + sortBy + ", Direction: " + direction + ", Keyword: " + keyword);

        Sort sort = Sort.by(direction.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage;
        if (keyword.isEmpty()) {
            productPage = productService.getAllProducts(pageable);
        } else {
            productPage = productService.searchProductsByName(keyword, pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("keyword", keyword);

        return "client/pagechualamcontroller/shop";
    }

    @GetMapping("/myaccount")
    public String getMyAccountPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        User user = this.userService.getUserByEmail(email);
        model.addAttribute("user", user);

        // Lấy danh sách đơn hàng của user
        List<Order> orders = orderRepository.findByUser(user);
        model.addAttribute("orders", orders);

        return "client/homepage/my-account";
    }

    @GetMapping("/myaccount/update")
    public String getUpdateAccount(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        User user = this.userService.getUserByEmail(email);
        model.addAttribute("user", user);
        return "client/homepage/my-account-update";
    }

    @PostMapping("/myaccount/update")
    public String postUpdateAccount(@ModelAttribute("user") User updatedUser, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        User user = this.userService.getUserByEmail(email);

        if (user != null) {
            user.setHoVaTen(updatedUser.getHoVaTen());
            user.setDiaChi(updatedUser.getDiaChi());
            user.setSoDienThoai(updatedUser.getSoDienThoai());
            user.setGioiTinh(updatedUser.getGioiTinh());
            user.setNgaySinh(updatedUser.getNgaySinh());
            this.userService.handleSaveUser(user);
            session.setAttribute("hoVaTen", user.getHoVaTen());
            model.addAttribute("message", "Cập nhật thông tin thành công!");
        }
        model.addAttribute("user", user);
        return "client/homepage/my-account";
    }

    @PostMapping("/myaccount/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");
        User user = this.userService.getUserByEmail(email);

        if (user != null) {
            if (!passwordEncoder.matches(currentPassword, user.getMatKhau())) {
                model.addAttribute("error", "Mật khẩu hiện tại không đúng!");
                model.addAttribute("user", user);
                return "client/homepage/my-account";
            } else if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "Mật khẩu mới và xác nhận không khớp!");
                model.addAttribute("user", user);
                return "client/homepage/my-account";
            } else {
                String hashPass = passwordEncoder.encode(newPassword);
                user.setMatKhau(hashPass);
                this.userService.handleSaveUser(user);
                // Hủy session hiện tại
                session.invalidate();
                // Thông báo thành công qua RedirectAttributes
                redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
                return "redirect:/login";
            }
        }
        model.addAttribute("error", "Không tìm thấy thông tin người dùng!");
        model.addAttribute("user", user);
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
    public String postProductToMyCartPage(@PathVariable("id") int productId,
            @RequestParam(value = "soLuongTrongGio", defaultValue = "1") int soLuongTrongGio,
            HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            return "redirect:/login";
        }

        String email = (String) session.getAttribute("email");
        User user = this.userService.getUserByEmail(email);
        Optional<Product> productOpt = productService.getProductById(productId);

        if (productOpt.isEmpty()) {
            return "redirect:/";
        }

        Product product = productOpt.get();
        int currentStock = product.getSlTonKho();
        Optional<Cart> existingCartOpt = cartRepository.findCartByUserAndProduct(user, product);
        int currentQuantityInCart = existingCartOpt.isPresent() ? existingCartOpt.get().getSoLuongTrongGio() : 0;

        if (currentStock < currentQuantityInCart + soLuongTrongGio) {
            String errorMessage = "Không đủ hàng trong kho. Còn lại: " + currentStock;
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("product", product);
            return "client/product/detail";
        }

        productService.handleCheckAndAddProductInCart(email, productId, soLuongTrongGio);
        session.setAttribute("totalQuantityInCart", user.getTotalQuantityInCart());
        return "redirect:/cart";
    }

    @PostMapping("/delete-cart-product/{id}")
    public String postDeleteProductToMyCartPage(@PathVariable("id") int productId, HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");

        // Gọi phương thức xử lý xóa sản phẩm khỏi giỏ hàng
        this.productService.removeProductFromCart(email, productId, session);
        return "redirect:/cart";
    }

    @GetMapping("/thanhtoan")
    public String getMythanhtoanPage(Model model, HttpServletRequest request) {
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
        return "client/payment/thanhtoan";
    }

    @PostMapping("/dat-hang")
    public String postDatHang(HttpServletRequest request, Model model) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("email") == null) {
                model.addAttribute("errorMessage", "Bạn cần đăng nhập để đặt hàng!");
                return "client/payment/thanhtoan";
            }

            String email = (String) session.getAttribute("email");
            User user = this.userService.getUserByEmail(email);
            if (user == null) {
                model.addAttribute("errorMessage", "Không tìm thấy thông tin người dùng.");
                return "client/payment/thanhtoan";
            }

            // Lấy giỏ hàng
            List<Cart> cartItems = cartRepository.findByUser(user);
            if (cartItems.isEmpty()) {
                model.addAttribute("errorMessage", "Giỏ hàng trống, không thể đặt hàng.");
                return "client/payment/thanhtoan";
            }
            // Kiểm tra số lượng tồn kho trước khi đặt hàng
            for (Cart item : cartItems) {
                Product product = item.getProduct();
                int requestedQuantity = item.getSoLuongTrongGio();
                int currentStock = product.getSlTonKho();

                if (currentStock < requestedQuantity) {
                    model.addAttribute("errorMessage",
                            "Sản phẩm " + product.getTenSP() + " không đủ tồn kho. Còn lại: " + currentStock);
                    return "client/payment/thanhtoan";
                }
            }

            float totalPrice = 0;
            for (Cart ci : cartItems) {
                totalPrice += ci.getProduct().getGia() * ci.getSoLuongTrongGio();
            }

            // Tạo đơn hàng mới
            Order newOrder = new Order();
            newOrder.setUser(user);
            newOrder.setNgayOrder(new Date());
            newOrder.setTrangThai("PENDING");
            newOrder.setTongGiaTri(totalPrice);
            newOrder = orderRepository.save(newOrder);

            // Lưu chi tiết đơn hàng
            for (Cart item : cartItems) {
                Product product = item.getProduct();
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(newOrder);
                orderDetail.setProduct(item.getProduct());
                orderDetail.setSoLuong(item.getSoLuongTrongGio());
                orderDetail.setDonGia(item.getProduct().getGia());
                orderDetail.setTongGia(item.getProduct().getGia() * item.getSoLuongTrongGio());
                orderDetailRepository.save(orderDetail);

                // Cập nhật số lượng tồn kho
                product.setSlTonKho(product.getSlTonKho() - item.getSoLuongTrongGio());
                productService.handleSaveProduct(product); // Lưu thay đổi vào database
            }

            // Xóa giỏ hàng sau khi đặt hàng
            cartRepository.deleteAll(cartItems);

            // Cập nhật session
            session.setAttribute("totalQuantityInCart", 0);
            user.setTotalQuantityInCart(0);
            userService.handleSaveUser(user);

            return "redirect:/";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi đặt hàng. Vui lòng thử lại!");
            return "client/payment/thanhtoan";
        }
    }

    @GetMapping("/404")
    public String getMy404Page(Model model) {
        return "client/pagechualamcontroller/404";
    }

    @GetMapping("/contact")
    public String getMycontactPage(Model model) {
        return "client/contact/contact";
    }

    @PostMapping("/contact")
    public String postContact(Model model,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("message") String message) {
        String subject = "Liên hệ từ " + name;
        String body = "Email: " + email + "\nNội dung: " + message;

        // Gửi email qua hàng đợi
        mailService.push("bangtnfx20011@funix.edu.vn", subject, body);

        model.addAttribute("successMessage", "Tin nhắn của bạn đã được gửi thành công!");
        return "client/contact/contact";
    }

    @GetMapping("/danhgia")
    public String getMydanhgiaPage(Model model) {
        return "client/pagechualamcontroller/danhgia";
    }

    @GetMapping("/login")
    public String getMydangnhapPage(Model model, HttpServletRequest request) {

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
        String activationCode = userService.generateActivationCode();
        user.setActivationCode(activationCode);
        user.setActivated(false); // Chưa kích hoạt
        this.userService.handleSaveUser(user);

        // Gửi email kích hoạt
        String activationLink = "http://localhost:8080/client/auth/activate?code=" + activationCode;
        String emailBody = "Chào " + user.getHoVaTen() + ",\n\n" +
                "Cảm ơn bạn đã đăng ký tại PetShop. Vui lòng nhấp vào liên kết sau để kích hoạt tài khoản:\n" +
                activationLink + "\n\n" +
                "Trân trọng,\nPetShop Team";
        mailService.push(user.getEmail(), "Kích hoạt tài khoản PetShop", emailBody);

        model.addAttribute("message", "Đăng ký thành công! Vui lòng kiểm tra email để kích hoạt tài khoản.");

        return "redirect:/client/auth/dangnhap";
    }

    @GetMapping("/client/auth/activate")
    public String activateAccount(@RequestParam("code") String code, Model model) {
        User user = userService.activateUser(code);
        if (user != null) {
            model.addAttribute("message", "Tài khoản của bạn đã được kích hoạt thành công! Vui lòng đăng nhập.");
        } else {
            model.addAttribute("message", "Mã kích hoạt không hợp lệ hoặc tài khoản đã được kích hoạt.");
        }
        return "client/auth/dangnhap";
    }

    @GetMapping("/client/auth/quenmatkhau")
    public String getMyquenmatkhauPage(Model model) {
        return "client/auth/quenmatkhau";
    }

    @PostMapping("/client/auth/quenmatkhau")
    public String forgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Email không tồn tại!");
            return "redirect:/client/auth/quenmatkhau";
        }

        String resetCode = userService.generateActivationCode();
        user.setActivationCode(resetCode);
        userService.handleSaveUser(user);
        User savedUser = userService.getUserByEmail(email);
        System.out.println("Saved reset code: " + savedUser.getActivationCode());
        String resetLink = "http://localhost:8080/resetpass?code=" + resetCode;
        String emailBody = "Chào " + user.getHoVaTen() + ",\n\n" +
                "Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng nhấp vào liên kết sau để đặt lại:\n" +
                resetLink + "\n\n" +
                "Nếu không phải bạn yêu cầu, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\nPetShop Team";
        mailService.push(email, "Đặt lại mật khẩu PetShop", emailBody);

        redirectAttributes.addFlashAttribute("message", "Link đặt lại mật khẩu đã được gửi đến email của bạn!");
        System.out.println("Message set: Link đặt lại mật khẩu đã được gửi đến email của bạn!");
        return "redirect:/login";
    }

    @GetMapping("/resetpass")
    public String getMyresetpassPage(Model model) {
        return "client/auth/resetpass";
    }

    @PostMapping("/resetpass")
    public String resetPassword(@RequestParam("code") String code,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {
        System.out.println("Received reset code from form: " + code);
        User user = userService.resetPassword(code, password);
        if (user != null) {
            redirectAttributes.addFlashAttribute("message", "Mật khẩu đã được đặt lại thành công! Vui lòng đăng nhập.");
            System.out.println("Message set: Mật khẩu đã được đặt lại thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Mã đặt lại không hợp lệ!");
            System.out.println("Error set: Mã đặt lại không hợp lệ! Code: " + code);
        }
        return "redirect:/login";
    }
}