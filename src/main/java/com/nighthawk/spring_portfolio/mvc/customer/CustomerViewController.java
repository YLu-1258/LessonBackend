package com.nighthawk.spring_portfolio.mvc.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

// Built using article: https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/mvc.html
// or similar: https://asbnotebook.com/2020/04/11/spring-boot-thymeleaf-form-validation-example/
@Controller
@RequestMapping("/mvc/customer")
public class CustomerViewController {
    // Autowired enables Control to connect HTML and POJO Object to database easily for CRUD
    @Autowired
    private CustomerDetailsService repository;

    @GetMapping("/read")
    public String customer(Model model) {
        List<Customer> list = repository.listAll();
        model.addAttribute("list", list);
        return "customer/read";
    }

    /*  The HTML template Forms and CustomerForm attributes are bound
        @return - template for customer form
        @param - Customer Class
    */
    @GetMapping("/create")
    public String customerAdd(Customer customer) {
        return "customer/create";
    }

    /* Gathers the attributes filled out in the form, tests for and retrieves validation error
    @param - Customer object with @Valid
    @param - BindingResult object
     */
    @PostMapping("/create")
    public String customerSave(@Valid Customer customer, BindingResult bindingResult) {
        // Validation of Decorated CustomerForm attributes
        if (bindingResult.hasErrors()) {
            return "customer/create";
        }
        repository.save(customer);
        // Redirect to next step
        return "redirect:/mvc/customer/read";
    }

    @GetMapping("/update/{id}")
    public String customerUpdate(@PathVariable("id") int id, Model model) {
        model.addAttribute("customer", repository.get(id));
        return "customer/update";
    }

    @PostMapping("/update")
    public String customerUpdateSave(@Valid Customer customer, BindingResult bindingResult) {
        // Validation of Decorated CustomerForm attributes
        if (bindingResult.hasErrors()) {
            return "customer/update";
        }
        repository.save(customer);

        // Redirect to next step
        return "redirect:/mvc/customer/read";
    }

    @GetMapping("/delete/{id}")
    public String customerDelete(@PathVariable("id") long id) {
        repository.delete(id);
        return "redirect:/mvc/customer/read";
    }

    @GetMapping("/search")
    public String customer() {
        return "customer/search";
    }

}