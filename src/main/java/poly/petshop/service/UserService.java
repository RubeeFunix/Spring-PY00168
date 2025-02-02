package poly.petshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import poly.petshop.domain.User;
import poly.petshop.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        List<User> users = this.userRepository.findAll();
        return users != null ? users : List.of(); // Trả về danh sách rỗng nếu `null`
    }

    public boolean phoneExists(String phone) {
        return userRepository.existsBySoDienThoai(phone);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean emailExists(String email, int userId) {
        return userRepository.existsByEmailAndUserIdNot(email, userId);
    }

    public boolean phoneExists(String phone, int userId) {
        return userRepository.existsBySoDienThoaiAndUserIdNot(phone, userId);
    }

    public List<User> getAllUsersByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findUserByEmail(email);
    }

    public User getUserById(int userId) {
        return this.userRepository.findById(userId);
    }

    public User handleSaveUser(User user) {
        User eric = this.userRepository.save(user);
        System.out.println(eric);
        return eric;
    }

    public void deletetUserById(int userId) {
        this.userRepository.deleteById(userId);
    }
}
