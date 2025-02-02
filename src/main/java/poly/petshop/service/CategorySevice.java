package poly.petshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import poly.petshop.domain.Category;
import poly.petshop.repository.CategoryRepository;

@Service
public class CategorySevice {

    private final CategoryRepository categoryRepository;

    public CategorySevice(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = this.categoryRepository.findAll();
        return categories != null ? categories : List.of(); // Trả về danh sách rỗng nếu `null`
    }

    // public List<Category> getAllUsersByEmail(String email) {
    // return this.userRepository.findByEmail(email);
    // }

    public Category getCategoryById(int categoryId) {
        return this.categoryRepository.findById(categoryId);
    }

    public Category handleSaveCategory(Category category) {
        Category cate = this.categoryRepository.save(category);
        System.out.println(cate);
        return cate;
    }

    public void deletetCategoryById(int categoryId) {
        this.categoryRepository.deleteById(categoryId);
    }
}
