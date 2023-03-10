package co.com.osorio.repositories;


import co.com.osorio.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM CUSTOMER c WHERE c.code= ?1")
    public Customer findCustomerByCode(String code);

    @Query("SELECT c FROM CUSTOMER c WHERE c.iban= ?1")
    public Customer findByAccount(String iban);

}
