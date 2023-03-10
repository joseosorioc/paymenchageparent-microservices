package co.com.osorio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableEurekaClient
@EnableJpaRepositories("co.com.osorio.repositories")
public class Transactions {

    public static void main(String[] args) {
        SpringApplication.run(Transactions.class);
    }

}