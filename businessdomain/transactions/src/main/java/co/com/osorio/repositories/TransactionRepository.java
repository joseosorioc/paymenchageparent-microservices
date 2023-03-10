package co.com.osorio.repositories;

import co.com.osorio.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    @Query("SELECT p FROM TRANSACTION p WHERE p.AccountIban =?1 ")
    List<Transaction> getAllTransactionsByIban(String iban );

}
