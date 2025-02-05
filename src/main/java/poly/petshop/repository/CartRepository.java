package poly.petshop.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.petshop.domain.Cart;
import poly.petshop.domain.Product;
import poly.petshop.domain.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findCartByUserAndProduct(User user, Product product);

    List<Cart> findByUser(User user);
}
