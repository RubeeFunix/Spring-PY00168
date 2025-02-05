package poly.petshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import poly.petshop.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product save(Product product);

    void deleteById(int productId);

    boolean existsBysku(String sku);

    List<Product> findAll();

    Optional<Product> findById(int productId);
}
