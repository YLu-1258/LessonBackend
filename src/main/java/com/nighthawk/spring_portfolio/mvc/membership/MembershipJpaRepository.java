package com.nighthawk.spring_portfolio.mvc.membership;

import java.util.List;

import jakarta.transaction.Transactional;

import com.nighthawk.spring_portfolio.mvc.customer.Customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipJpaRepository extends JpaRepository<Membership, Long> {
    List<Customer> findByPersonId(Long id);

    @Transactional
    void deleteByPersonId(long id);
}

