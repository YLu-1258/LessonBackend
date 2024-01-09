package com.nighthawk.spring_portfolio.mvc.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/customer")
public class CustomerApiController {
    //     @Autowired
    // private JwtTokenUtil jwtGen;
    /*
    #### RESTful API ####
    Resource: https://spring.io/guides/gs/rest-service/
    */

    // Autowired enables Control to connect POJO Object through JPA
    @Autowired
    private CustomerJpaRepository repository;

    @Autowired
    private CustomerDetailsService CustomerDetailsService;

    /*
    GET List of People
     */
    @GetMapping("/")
    public ResponseEntity<List<Customer>> getPeople() {
        return new ResponseEntity<>( repository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }

    /*
    GET individual Customer using ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable long id) {
        Optional<Customer> optional = repository.findById(id);
        if (optional.isPresent()) {  // Good ID
            Customer Customer = optional.get();  // value from findByID
            return new ResponseEntity<>(Customer, HttpStatus.OK);  // OK HTTP response: status code, headers, and body
        }
        // Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);       
    }

    /*
    DELETE individual Customer using ID
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable long id) {
        Optional<Customer> optional = repository.findById(id);
        if (optional.isPresent()) {  // Good ID
            Customer Customer = optional.get();  // value from findByID
            repository.deleteById(id);  // value from findByID
            return new ResponseEntity<>(Customer, HttpStatus.OK);  // OK HTTP response: status code, headers, and body
        }
        // Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
    }

    /*
    POST Aa record by Requesting Parameters from URI
     */
    @PostMapping( "/post")
    public ResponseEntity<Object> postCustomer(@RequestParam("email") String email,
                                             @RequestParam("password") String password,
                                             @RequestParam("name") String name,
                                             @RequestParam("dob") String dobString) {
        Date dob;
        try {
            dob = new SimpleDateFormat("MM-dd-yyyy").parse(dobString);
        } catch (Exception e) {
            return new ResponseEntity<>(dobString +" error; try MM-dd-yyyy", HttpStatus.BAD_REQUEST);
        }
        // A Customer object WITHOUT ID will create a new record with default products as student
        Customer Customer = new Customer(email, password, name, dob);
        CustomerDetailsService.save(Customer);
        return new ResponseEntity<>(email +" is created successfully", HttpStatus.CREATED);
    }

    /*
    The CustomerSearch API looks across database for partial match to term (k,v) passed by RequestEntity body
     */
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> CustomerSearch(@RequestBody final Map<String,String> map) {
        // extract term from RequestEntity
        String term = (String) map.get("term");

        // JPA query to filter on term
        List<Customer> list = repository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);

        // return resulting list and status, error checking should be added
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /*
    The CustomerStats API adds stats by Date to Customer table 
    */
    @PostMapping(value = "/setStats", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> CustomerStats(@RequestBody final Map<String,Object> stat_map) {
        // find ID
        long id=Long.parseLong((String)stat_map.get("id"));  
        Optional<Customer> optional = repository.findById((id));
        if (optional.isPresent()) {  // Good ID
            Customer Customer = optional.get();  // value from findByID

            // Extract Attributes from JSON
            Map<String, Object> attributeMap = new HashMap<>();
            for (Map.Entry<String,Object> entry : stat_map.entrySet())  {
                // Add all attribute other thaN "date" to the "attribute_map"
                if (!entry.getKey().equals("date") && !entry.getKey().equals("id"))
                    attributeMap.put(entry.getKey(), entry.getValue());
            }

            // Set Date and Attributes to SQL HashMap
            Map<String, Map<String, Object>> date_map = new HashMap<>();
            date_map.put( (String) stat_map.get("date"), attributeMap );
            Customer.setOrderHistory(date_map);  // BUG, needs to be customized to replace if existing or append if new
            repository.save(Customer);  // conclude by writing the stats updates

            // return Customer with update Stats
            return new ResponseEntity<>(Customer, HttpStatus.OK);
        }
        // return Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
    }
}
