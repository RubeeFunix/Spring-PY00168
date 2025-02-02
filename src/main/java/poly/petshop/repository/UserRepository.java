package poly.petshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import poly.petshop.domain.User;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User save(User eric);

    void deleteById(int userId);

    List<User> findAll();

    User findById(int userId);

    List<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsBySoDienThoai(String phone);

    boolean existsByEmailAndUserIdNot(String email, int userId);

    boolean existsBySoDienThoaiAndUserIdNot(String soDienThoai, int userId);

    User findUserByEmail(String email);
}
