package com.nighthawk.spring_portfolio.mvc.customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface  CustomerProductJpaRepository extends JpaRepository<CustomerProducts, Long> {
    CustomerProducts findByName(String name);
}
