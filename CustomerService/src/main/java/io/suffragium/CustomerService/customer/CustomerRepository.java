package io.suffragium.CustomerService.customer;

import io.suffragium.common.entity.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmailContaining(String email);
    Optional<Customer> findById(@Param("id") Long id);
}
