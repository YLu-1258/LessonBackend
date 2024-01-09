package com.nighthawk.spring_portfolio.mvc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.nighthawk.spring_portfolio.mvc.jokes.Jokes;
import com.nighthawk.spring_portfolio.mvc.jokes.JokesJpaRepository;
// import com.nighthawk.spring_portfolio.mvc.note.Note;
// import com.nighthawk.spring_portfolio.mvc.note.NoteJpaRepository;
// import com.nighthawk.spring_portfolio.mvc.person.Person;
// import com.nighthawk.spring_portfolio.mvc.person.PersonDetailsService;
import com.nighthawk.spring_portfolio.mvc.membership.Membership;
import com.nighthawk.spring_portfolio.mvc.membership.MembershipJpaRepository;
import com.nighthawk.spring_portfolio.mvc.customer.Customer;
import com.nighthawk.spring_portfolio.mvc.customer.CustomerDetailsService;

import java.util.List;

@Component
@Configuration // Scans Application for ModelInit Bean, this detects CommandLineRunner
public class ModelInit {  
    @Autowired JokesJpaRepository jokesRepo;
    // @Autowired NoteJpaRepository noteRepo;
    @Autowired MembershipJpaRepository membershipRepo;
    // @Autowired PersonDetailsService personService;
    @Autowired CustomerDetailsService customerService;

    @Bean
    CommandLineRunner run() {  // The run() method will be executed after the application starts
        return args -> {

            // Joke database is populated with starting jokes
            String[] jokesArray = Jokes.init();
            for (String joke : jokesArray) {
                List<Jokes> jokeFound = jokesRepo.findByJokeIgnoreCase(joke);  // JPA lookup
                if (jokeFound.size() == 0)
                    jokesRepo.save(new Jokes(null, joke, 0, 0)); //JPA save
            }

            // // Person database is populated with test data
            // Person[] personArray = Person.init();
            // for (Person person : personArray) {
            //     //findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase
            //     List<Person> personFound = personService.list(person.getName(), person.getEmail());  // lookup
            //     if (personFound.size() == 0) {
            //         personService.save(person);  // save

            //         // Each "test person" starts with a "test note"
            //         String text = "Test " + person.getEmail();
            //         Note n = new Note(text, person);  // constructor uses new person as Many-to-One association
            //         noteRepo.save(n);  // JPA Save                  
            //     }
            // }

            // Customer database is populated with test data
            Customer[] customerArray = Customer.init();
            for (Customer customer : customerArray) {
                //findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase
                List<Customer> customerFound = customerService.list(customer.getName(), customer.getEmail());  // lookup
                if (customerFound.size() == 0) {
                    customerService.save(customer);  // save

                    // Each "test person" starts with a "test note"
                    String text = "Standard membership";
                    Membership m = new Membership(text, customer);  // constructor uses new person as Many-to-One association
                    membershipRepo.save(m);  // JPA Save                  
                }
            }

        };
    }
}

