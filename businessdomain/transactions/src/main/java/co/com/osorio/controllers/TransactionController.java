package co.com.osorio.controllers;

import co.com.osorio.models.Transaction;
import co.com.osorio.services.TransactionService;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    //webClient requires HttpClient library to work propertly
    HttpClient client = HttpClient.create()
            //Connection Timeout: is a period within which a connection between a client and a server must be established
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            //Response Timeout: The maximun time we wait to receive a response after sending a request
            .responseTimeout(Duration.ofSeconds(1))
            // Read and Write Timeout: A read timeout occurs when no data was read within a certain
            //period of time, while the write timeout when a write operation cannot finish at a specific time
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable(name = "id") Long id ){
        return new ResponseEntity<>(transactionService.getTransaction(id), HttpStatus.OK) ;
    }

    @GetMapping(value = "/customers")
    public List<?> getAll(){
        return transactionService.getAllTransactions();
    }

    @PostMapping
    public ResponseEntity<?> saveTransaction(@RequestBody Transaction transaction){

        return  ResponseEntity.ok( transactionService.createTransaction(transaction) );
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable("id") Long id, @RequestBody Transaction transaction ){


        return ResponseEntity.ok( transactionService.updateTransaction( id, transaction) );
    }

    @GetMapping(value = "/customer/transactions")
    public List<Transaction> getAllTransactionByIban(@RequestParam String ibanAccount){

        return transactionService.getAllTransactionsByIban(ibanAccount);

    }



}
