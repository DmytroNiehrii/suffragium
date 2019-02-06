package io.suffragium.CustomerService.customer;

import io.suffragium.common.entity.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmailContaining(String email);
}
