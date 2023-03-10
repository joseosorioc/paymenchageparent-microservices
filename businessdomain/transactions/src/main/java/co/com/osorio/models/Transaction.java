package co.com.osorio.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;


@Data
@Entity(name = "TRANSACTION")
public class Transaction {

    @Id
    private Long id;

    private String reference;

    private String AccountIban;

    private LocalDate date;

    private Double amount;

    private Double fee;

    private String description;

    private String Status;

    private String Channel;


}
