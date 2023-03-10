package co.com.osorio.models;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "CUSTOMER")
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;

    private String phone;
    private String iban;
    private String surname;
    private String address;

    @OneToMany(fetch =  FetchType.LAZY, mappedBy = "customer",
            cascade = CascadeType.ALL, orphanRemoval = true )
    private List<CustomerProduct> products;

    @Transient
    private List<?> transactions;




}
