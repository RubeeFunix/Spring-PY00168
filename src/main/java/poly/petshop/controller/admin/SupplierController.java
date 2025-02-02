package poly.petshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import poly.petshop.domain.Supplier;
import poly.petshop.service.SupplierService;

@Controller
public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(
            SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/admin/supplier")
    public String SupplierPage(Model model) {
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "admin/supplier/show";
    }

    @GetMapping("/admin/supplier/{supplierId}")
    public String GetSupplierDetailPage(@PathVariable("supplierId") int supplierId, Model model) {
        Supplier supplier = supplierService.getSupplierById(supplierId);
        model.addAttribute("supplier", supplier);
        model.addAttribute("supplierId", supplierId);
        return "admin/supplier/detail";
    }

    // Trang tạo mới category
    @GetMapping("/admin/supplier/create")
    public String SupplierPageCreate(@ModelAttribute("supplier") Supplier supplier, Model model) {
        return "admin/supplier/create";
    }

    // Table category đã tạo
    @PostMapping("/admin/supplier/create")
    public String PageAlreadyCreateSupplier(@ModelAttribute("supplier") Supplier supplier, Model model) {
        System.out.println("Supplier Created: " + supplier.toString());
        this.supplierService.handleSaveSupplier(supplier);
        return "redirect:/admin/supplier";
    }

    // // Trang update user
    // @GetMapping("/admin/category/update/{categoryId}")
    // public String GetCategoryUpdatePage(@PathVariable("categoryId") int
    // categoryId, Model model) {
    // Category currentCategory = categorySevice.getCategoryById(categoryId);
    // model.addAttribute("category", currentCategory);
    // model.addAttribute("categoryId", categoryId);
    // return "admin/category/update";
    // }

    // @PostMapping("/admin/category/update")
    // public String PostCategoryUpdatePage(@ModelAttribute("category") Category
    // thisCategory, Model model) {
    // Category currentCategory =
    // categorySevice.getCategoryById(thisCategory.getCategoryId());
    // if (currentCategory != null) {
    // currentCategory.setCategoryName(thisCategory.getCategoryName());
    // this.categorySevice.handleSaveCategory(currentCategory);

    // }
    // return "redirect:/admin/category";
    // }

    // // Trang delete category
    // @GetMapping("/admin/category/delete/{categoryId}")
    // public String GetCategoryDeletePage(@PathVariable("categoryId") int
    // categoryId, Model model) {
    // Category category = categorySevice.getCategoryById(categoryId);
    // model.addAttribute("category", category);
    // model.addAttribute("categoryId", categoryId);
    // return "admin/category/delete";
    // }

    // @PostMapping("/admin/category/delete")
    // public String PostCategoryDeletePage(@ModelAttribute("category") Category
    // thisCategory, Model model) {
    // this.categorySevice.deletetCategoryById(thisCategory.getCategoryId());
    // return "redirect:/admin/category";
    // }
}
