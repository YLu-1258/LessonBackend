package com.nighthawk.spring_portfolio.mvc.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
This class has an instance of Java Persistence API (JPA)
-- @Autowired annotation. Allows Spring to resolve and inject collaborating beans into our bean.
-- Spring Data JPA will generate a proxy instance
-- Below are some CRUD methods that we can use with our database
*/
@Service
@Transactional
public class CustomerDetailsService implements UserDetailsService {  // "implements" ties ModelRepo to Spring Security
    // Encapsulate many object into a single Bean (Customer, Products, and Scrum)
    @Autowired  // Inject CustomerJpaRepository
    private CustomerJpaRepository customerJpaRepository;
    @Autowired  // Inject ProductJpaRepository
    private CustomerProductJpaRepository customerProductJpaRepository;
    // @Autowired  // Inject PasswordEncoder
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /* UserDetailsService Overrides and maps Customer & Products POJO into Spring Security */
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerJpaRepository.findByEmail(email); // setting variable user equal to the method finding the username in the database
        if(customer==null) {
			throw new UsernameNotFoundException("User not found with username: " + email);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        customer.getProducts().forEach(product -> { //loop through products
            authorities.add(new SimpleGrantedAuthority(product.getName())); //create a SimpleGrantedAuthority by passed in product, adding it all to the authorities list, list of products gets past in for spring security
        });
        // train spring security to User and Authorities
        return new org.springframework.security.core.userdetails.User(customer.getEmail(), customer.getPassword(), authorities);
    }

    /* Customer Section */

    public  List<Customer>listAll() {
        return customerJpaRepository.findAllByOrderByNameAsc();
    }

    // custom query to find match to name or email
    public  List<Customer>list(String name, String email) {
        return customerJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(name, email);
    }

    // custom query to find anything containing term in name or email ignoring case
    public  List<Customer>listLike(String term) {
        return customerJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);
    }

    // custom query to find anything containing term in name or email ignoring case
    public  List<Customer>listLikeNative(String term) {
        String like_term = String.format("%%%s%%",term);  // Like required % rappers
        return customerJpaRepository.findByLikeTermNative(like_term);
    }

    // encode password prior to sava
    public void save(Customer customer) {
        customer.setPassword(passwordEncoder().encode(customer.getPassword()));
        customerJpaRepository.save(customer);
    }

    public Customer get(long id) {
        return (customerJpaRepository.findById(id).isPresent())
                ? customerJpaRepository.findById(id).get()
                : null;
    }

    public Customer getByEmail(String email) {
        return (customerJpaRepository.findByEmail(email));
    }

    public void delete(long id) {
        customerJpaRepository.deleteById(id);
    }

    public void defaults(String password, String productName) {
        for (Customer customer: listAll()) {
            if (customer.getPassword() == null || customer.getPassword().isEmpty() || customer.getPassword().isBlank()) {
                customer.setPassword(passwordEncoder().encode(password));
            }
            if (customer.getProducts().isEmpty()) {
                CustomerProducts product = customerProductJpaRepository.findByName(productName);
                if (product != null) { // verify product
                    customer.getProducts().add(product);
                }
            }
        }
    }


    /* Products Section */

    public void saveProduct(CustomerProducts product) {
        CustomerProducts productObj = customerProductJpaRepository.findByName(product.getName());
        if (productObj == null) {  // only add if it is not found
            customerProductJpaRepository.save(product);
        }
    }

    public  List<CustomerProducts>listAllProducts() {
        return customerProductJpaRepository.findAll();
    }

    public CustomerProducts findProduct(String productName) {
        return customerProductJpaRepository.findByName(productName);
    }

    public void addProductToCustomer(String email, String productName) { // by passing in the two strings you are giving the user that certain product
        Customer customer = customerJpaRepository.findByEmail(email);
        if (customer != null) {   // verify customer
            CustomerProducts product = customerProductJpaRepository.findByName(productName);
            if (product != null) { // verify product
                boolean addProduct = true;
                for (CustomerProducts productObj : customer.getProducts()) {    // only add if user is missing product
                    if (productObj.getName().equals(productName)) {
                        addProduct = false;
                        break;
                    }
                }
                if (addProduct) customer.getProducts().add(product);   // everything is valid for adding product
            }
        }
    }
    
}