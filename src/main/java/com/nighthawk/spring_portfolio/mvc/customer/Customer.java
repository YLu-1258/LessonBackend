package com.nighthawk.spring_portfolio.mvc.customer;

import static jakarta.persistence.FetchType.EAGER;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
Customer is a POJO, Plain Old Java Object.
First set of annotations add functionality to POJO
--- @Setter @Getter @ToString @NoArgsConstructor @RequiredArgsConstructor
The last annotation connect to database
--- @Entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Convert(attributeName ="customer", converter = JsonType.class)
public class Customer {

    // automatic unique identifier for Customer record
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // email, password, products are key attributes to login and authentication
    @NotEmpty
    @Size(min=5)
    @Column(unique=true)
    @Email
    private String email;

    @NotEmpty
    private String password;

    // @NonNull, etc placed in params of constructor: "@NonNull @Size(min = 2, max = 30, message = "Name (2 to 30 chars)") String name"
    @NonNull
    @Size(min = 2, max = 30, message = "Name (2 to 30 chars)")
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastVisited;

    // To be implemented
    @ManyToMany(fetch = EAGER)
    private Collection<CustomerProducts> products = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String,Map<String, Object>> orderHistory = new HashMap<>();

    // Constructor used when building object from an API
    public Customer(String email, String password, String name, Date lastVisited) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastVisited = lastVisited;
    }

    // A custom getter to return age from lastVisited attribute
    public int get() {
        if (this.lastVisited != null) {
            LocalDate birthDay = this.lastVisited.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return Period.between(birthDay, LocalDate.now()).getYears(); }
        return -1;
    }

    // Initialize static test data 
    public static Customer[] init() {

        // basics of class construction
        Customer p1 = new Customer();
        p1.setName("Thomas Edison");
        p1.setEmail("toby@gmail.com");
        p1.setPassword("123Toby!");
        // adding Note to notes collection
        try {  // All data that converts formats could fail
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("01-01-1840");
            p1.setLastVisited(d);
        } catch (Exception e) {
            // no actions as lastVisited default is good enough
        }

        Customer p2 = new Customer();
        p2.setName("Alexander Graham Bell");
        p2.setEmail("lexb@gmail.com");
        p2.setPassword("123LexB!");
        try {
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("01-01-1845");
            p2.setLastVisited(d);
        } catch (Exception e) {
        }

        Customer p3 = new Customer();
        p3.setName("Nikola Tesla");
        p3.setEmail("niko@gmail.com");
        p3.setPassword("123Niko!");
        try {
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("01-01-1850");
            p3.setLastVisited(d);
        } catch (Exception e) {
        }

        Customer p4 = new Customer();
        p4.setName("Madam Currie");
        p4.setEmail("madam@gmail.com");
        p4.setPassword("123Madam!");
        try {
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("01-01-1860");
            p4.setLastVisited(d);
        } catch (Exception e) {
        }

        Customer p5 = new Customer();
        p5.setName("John Mortensen");
        p5.setEmail("jm1021@gmail.com");
        p5.setPassword("123Qwerty!");
        try {
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("10-21-1959");
            p5.setLastVisited(d);
        } catch (Exception e) {
        }

        // Array definition and data initialization
        Customer customers[] = {p1, p2, p3, p4, p5};
        for (Customer customer : customers) {
            Map<String, Object> orderMap = generateRandomOrder();
            customer.getOrderHistory().put("2023-12-17", orderMap);
        }
        return(customers);
    }
    private static Map<String, Object> generateRandomOrder() {
        Map<String, Object> orderMap = new HashMap<>();
        Random random = new Random();

        // Sample map of products and prices
        Map<String, Double> products = new HashMap<>();
        products.put("Carrots", 10.0);
        products.put("Spatula", 20.0);
        products.put("Pans", 45.0);
        products.put("Eggs", 9.0);
        products.put("Pistachios", 11.0);
        products.put("Moon Cakes", 25.0);
        products.put("Beef", 55.0);

        // Generate a random product and quantity for the order
        String randomProduct = getRandomElement(products.keySet());
        double randomPrice = products.get(randomProduct);
        int randomQuantity = random.nextInt(5) + 1; // Random quantity between 1 and 5

        orderMap.put("product", randomProduct);
        orderMap.put("price", randomPrice);
        orderMap.put("quantity", randomQuantity);

        return orderMap;
    }

    private static <T> T getRandomElement(Set<T> set) {
        int randomIndex = new Random().nextInt(set.size());
        Iterator<T> iterator = set.iterator();
        for (int i = 0; i < randomIndex; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    public static void main(String[] args) {
        // obtain Customer from initializer
        Customer customers[] = init();

        // iterate using "enhanced for loop"
        for( Customer customer : customers) {
            System.out.println(customer);  // print object
        }
    }

}