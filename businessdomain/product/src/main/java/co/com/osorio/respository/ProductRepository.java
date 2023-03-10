
package co.com.osorio.respository;


import co.com.osorio.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("ProductRepository")
public interface ProductRepository extends JpaRepository<Product, Long> {
    
}
