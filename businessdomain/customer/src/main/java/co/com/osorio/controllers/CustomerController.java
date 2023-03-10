package co.com.osorio.controllers;


import co.com.osorio.models.Customer;
import co.com.osorio.models.CustomerProduct;
import co.com.osorio.repositories.CustomerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "api/v1/customer")
@Slf4j
public class CustomerController {

    private final CustomerRepository customerRepository;

    // Cliente reactivo
    private final WebClient.Builder webClientBuilder;


    public CustomerController(CustomerRepository customerRepository, WebClient.Builder webClientBuilder) {
        this.customerRepository = customerRepository;
        this.webClientBuilder = webClientBuilder;
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
    public ResponseEntity<Customer> getCustomer(@PathVariable(name = "id") Long id ){
        return new ResponseEntity<>(customerRepository.findById(id).get(), HttpStatus.OK) ;
    }

    @GetMapping(value = "/customers")
    public List<Customer> getAll(){
        return customerRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> save( @RequestBody Customer customer){

        customer.getProducts().forEach( x -> x.setCustomer(customer) );
        Customer customer1 = customerRepository.save(customer);

        return  ResponseEntity.ok(customer1);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> put(@PathVariable("id") Long id, @RequestBody Customer customer){
        Customer save = customerRepository.save(customer);

        return ResponseEntity.ok(save);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id ){

        Optional<Customer> customerToDelete = customerRepository.findById(id);

        customerToDelete.ifPresent(customerRepository::delete);

    return ResponseEntity.ok().build();
    }


    @GetMapping("/full")
    public Customer getByCode( @RequestParam String code){
        Customer customer = customerRepository.findCustomerByCode(code);
        List<CustomerProduct> products = customer.getProducts();

        products.forEach(product -> {
            String gettingName = getProductName(product.getId());
            product.setNameProduct(gettingName);
        });

        customer.setTransactions(getTransactions(customer.getIban()));

        // returns

        return customer;

    }


    private String getProductName(long id) {

        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://business-domain-product/product")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8081/product"))
                .build();
        JsonNode block = build.method(HttpMethod.GET).uri("/" + id)
                .retrieve().bodyToMono(JsonNode.class).block();
        String name = block.get("name").asText();
        return name;
    }

    /**
     * Call Transaction Microservice and Find all transaction that belong to the account give
     * @param iban account number of the customer
     * @return All transaction that belong this account
     */
    private  List<?> getTransactions(String  iban) {
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://business-domain-transactions/api/v1/transaction")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();


   return build.method(HttpMethod.GET).uri(uriBuilder -> uriBuilder
                        .path("/customer/transactions")
                        .queryParam("ibanAccount", iban)
                        .build())
                        .retrieve()
                        .bodyToFlux(Object.class)
                        .collectList()
                        .block();


    }


}
