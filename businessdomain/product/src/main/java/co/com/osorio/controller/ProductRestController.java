/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.osorio.controller;


import co.com.osorio.entities.Product;
import co.com.osorio.respository.ProductRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/product")
public class ProductRestController {

    @Qualifier("ProductRepository")
   private final ProductRepository productRepository;

  @Value("${user.role}")
   private String role;


    public ProductRestController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @GetMapping()
    public List<Product> list() {
    System.out.println("Obteniendo todo con el rol: " + role);
        return productRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Product get(@PathVariable long id) {
        return productRepository.findById(id).get();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable long id, @RequestBody Product input) {
      Product find = productRepository.findById(id).get();   
        if(find != null){     
            find.setCode(input.getCode());
            find.setName(input.getName());
        }
        Product save = productRepository.save(find);
        return ResponseEntity.ok(save);
    }
    
    @PostMapping
    public ResponseEntity<?> post(@RequestBody Product input) {
        Product save = productRepository.save(input);
        return ResponseEntity.ok(save);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {  
        Optional<Product> findById = productRepository.findById(id);
        if(findById.get() != null){               
                  productRepository.delete(findById.get());  
        }
        return ResponseEntity.ok().build();
    }
    
}
