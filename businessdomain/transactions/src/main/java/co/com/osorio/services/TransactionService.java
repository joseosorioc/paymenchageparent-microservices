package co.com.osorio.services;

import co.com.osorio.models.Transaction;
import co.com.osorio.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {


    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    public Transaction createTransaction(Transaction transaction)
    {
       return this.transactionRepository.save(transaction);
    }

    public Transaction updateTransaction( Long id, Transaction transaction){

        Optional<Transaction> transactionToFind = transactionRepository.findById(id);

        transactionToFind.ifPresent( trs -> {
            trs.setFee(transaction.getFee());
            trs.setDate(transaction.getDate());
            trs.setChannel(transaction.getChannel());
            trs.setAmount(transaction.getAmount());
            trs.setDescription(transaction.getDescription());
            trs.setReference(transaction.getReference());

        });

       return transactionRepository.save(transactionToFind.get());
    }


    public List<Transaction> getAllTransactions(){
        return transactionRepository.findAll();
    }


    public Transaction getTransaction(Long id ){
        return transactionRepository.findById(id).get();
    }

    public List<Transaction> getAllTransactionsByIban(String iban){
        return transactionRepository.getAllTransactionsByIban(iban);
    }

}
