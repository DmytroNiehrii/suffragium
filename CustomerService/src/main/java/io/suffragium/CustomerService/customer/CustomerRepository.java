package io.suffragium.CustomerService.customer;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {
    Optional<Customer> findByEmailContaining(String email);
}
