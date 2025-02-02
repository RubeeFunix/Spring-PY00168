package poly.petshop.controller.admin;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import poly.petshop.domain.User;
import poly.petshop.repository.UserRepository;
import poly.petshop.service.UploadService;
import poly.petshop.service.UserService;

@Controller
public class UserController {

    // @Autowired
    // private HttpServletRequest request;

    private final UploadService uploadService; // Inject UploadService
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // Tiem DI viet dai
    public UserController(
            UserService userService,
            UploadService uploadService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.uploadService = uploadService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admin/user")
    public String UserPage(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user/show"; // Điều hướng về trang user
    }

    @GetMapping("/admin/user/{userId}")
    public String GetUserDetailPage(@PathVariable("userId") int userId, Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        System.out.println("Avatar path: " + user.getAvatar());
        return "admin/user/detail";
    }

    // Trang tạo mới user
    @GetMapping("/admin/user/create")
    public String UserPageCreate(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("userRoles", List.of("Admin", "User"));
        List<String> options = new ArrayList<>();
        options.add("Nam");
        options.add("Nữ");
        model.addAttribute("options", options);
        // model.addAttribute("gioiTinhs", List.of("Nam", "Nữ"));
        return "admin/user/create";
    }

    // Table user đã tạo
    @PostMapping("/admin/user/create")
    public String PageAlreadyCreateUser(
            @ModelAttribute("user") @Valid User user,
            BindingResult newUserBindingResult,
            Model model,
            @RequestParam("image") MultipartFile file) throws IOException {

        // validate
        List<FieldError> errors = newUserBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(error.getField() + " - " + error.getDefaultMessage());
        }
        if (newUserBindingResult.hasErrors()) {
            model.addAttribute("userRoles", List.of("Admin", "User"));
            List<String> options = new ArrayList<>();
            options.add("Nam");
            options.add("Nữ");
            model.addAttribute("options", options);

            return "admin/user/create";
        }
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("userRoles", List.of("Admin", "User"));
            List<String> options = new ArrayList<>();
            options.add("Nam");
            options.add("Nữ");
            model.addAttribute("options", options);
            model.addAttribute("error", "Email đã tồn tại! Vui lòng chọn email khác.");
            return "admin/user/create";
        }
        // Kiểm tra số điện thoại đã tồn tại
        if (userService.phoneExists(user.getSoDienThoai())) {
            model.addAttribute("userRoles", List.of("Admin", "User"));
            List<String> options = new ArrayList<>();
            options.add("Nam");
            options.add("Nữ");
            model.addAttribute("options", options);
            model.addAttribute("errorSDT", "Số điện thoại đã được sử dụng! Vui lòng nhập số khác.");
            return "admin/user/create";
        }
        //
        System.out.println("User Created: " + user.toString());

        // Thư mục lưu avatar
        String avatarDirectory = System.getProperty("user.dir") + "/src/main/resources/static/images/avatar";
        // Sau khi tiêm xong thì lấy ra xài
        String fileName = uploadService.handleSaveFile(file, avatarDirectory);
        String hashPass = this.passwordEncoder.encode(user.getMatKhau());
        user.setAvatar(fileName.toString());
        user.setMatKhau(hashPass);
        this.userService.handleSaveUser(user);

        // thông báo
        model.addAttribute("msg", "User created successfully with avatar: " + fileName);

        return "redirect:/admin/user"; // Điều hướng về trang user
    }

    // Trang update user
    @GetMapping("/admin/user/update/{userId}")
    public String GetUserUpdatePage(@PathVariable("userId") int userId, Model model) {
        User currentUser = userService.getUserById(userId);

        model.addAttribute("user", currentUser);
        model.addAttribute("userId", userId);
        model.addAttribute("userRoles", List.of("Admin", "User"));
        List<String> options = new ArrayList<>();
        options.add("Nam");
        options.add("Nữ");
        model.addAttribute("options", options);
        // model.addAttribute("gioiTinhs", List.of("Nam", "Nữ"));
        return "admin/user/update";
    }

    @PostMapping("/admin/user/update")
    public String PostUserUpdatePage(@ModelAttribute("user") User thisUser, Model model,
            @RequestParam("image") MultipartFile file) throws IOException {
        User currentUser = userService.getUserById(thisUser.getUserId());
        if (currentUser != null) {
            if (!file.isEmpty()) {
                // Thư mục lưu avatar
                String avatarDirectory = System.getProperty("user.dir") + "/src/main/resources/static/images/avatar";
                // Sau khi tiêm xong thì lấy ra xài
                String fileName = uploadService.handleSaveFile(file, avatarDirectory);
                currentUser.setAvatar(fileName);
            }
            // Kiểm tra email đã tồn tại nhưng bỏ qua chính user hiện tại
            if (userService.emailExists(thisUser.getEmail(), thisUser.getUserId())) {
                model.addAttribute("userRoles", List.of("Admin", "User"));
                model.addAttribute("options", List.of("Nam", "Nữ"));
                model.addAttribute("error", "Email đã được sử dụng! Vui lòng chọn email khác.");
                return "admin/user/update";
            }

            // Kiểm tra số điện thoại đã tồn tại nhưng bỏ qua chính user hiện tại
            if (userService.phoneExists(thisUser.getSoDienThoai(), thisUser.getUserId())) {
                model.addAttribute("userRoles", List.of("Admin", "User"));
                model.addAttribute("options", List.of("Nam", "Nữ"));
                model.addAttribute("errorSDT", "Số điện thoại đã được sử dụng! Vui lòng nhập số khác.");
                return "admin/user/update";
            }
            currentUser.setDiaChi(thisUser.getDiaChi());
            currentUser.setHoVaTen(thisUser.getHoVaTen());
            currentUser.setSoDienThoai(thisUser.getSoDienThoai());
            currentUser.setUserRole(thisUser.getUserRole());
            currentUser.setGioiTinh(thisUser.getGioiTinh());

            this.userService.handleSaveUser(currentUser);

        }
        return "redirect:/admin/user";
    }

    // Trang delete user
    @GetMapping("/admin/user/delete/{userId}")
    public String GetUserDeletePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        return "admin/user/delete";
    }

    @PostMapping("/admin/user/delete")
    public String PostUserDeletePage(@ModelAttribute("user") User thisUser, Model model) {
        this.userService.deletetUserById(thisUser.getUserId());
        return "redirect:/admin/user";
    }

}

// @RestController
// public class UserController {

// // DI: dependence injection
// private UserService userService;

// public UserController(UserService userService) {
// this.userService = userService;
// }

// @GetMapping("")
// public String getHomePage() {
// return this.userService.handleHello();
// }
// }