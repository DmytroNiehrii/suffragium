package io.suffragium.CustomerService.controller;

import io.suffragium.CustomerService.customer.Customer;
import io.suffragium.CustomerService.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/hal+json")
public class CustomerRestController {
    private  final CustomerResourceAssembler customerResourceAssembler;
    private CustomerRepository customerRepository;

    @Autowired
    CustomerRestController(CustomerResourceAssembler customerResourceAssembler, CustomerRepository customerRepository) {
        this.customerResourceAssembler = customerResourceAssembler;
        this.customerRepository = customerRepository;
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    ResponseEntity<?> options() {
        return ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.HEAD,
                        HttpMethod.OPTIONS, HttpMethod.PUT, HttpMethod.DELETE)
                .build();
    }

    @GetMapping
    ResponseEntity<Resources<Object>> root() {
        Resources<Object> objects = new Resources<>(Collections.emptyList());
        URI uri = MvcUriComponentsBuilder
                .fromMethodCall(MvcUriComponentsBuilder.on(getClass()).getCollection())
                .build().toUri();
        Link link = new Link(uri.toString(), "customers");
        objects.add(link);
        return ResponseEntity.ok(objects);
    }

    @GetMapping("/customers")
    ResponseEntity<Resources<Resource<Customer>>> getCollection() {
        List<Resource<Customer>> collect = this.customerRepository.findAll().stream()
                .map(customerResourceAssembler::toResource)
                .collect(Collectors.<Resource<Customer>>toList());
        Resources<Resource<Customer>> resources = new Resources<>(collect);
        URI self = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        resources.add(new Link(self.toString(), "self"));
        return ResponseEntity.ok(resources);
    }

    @GetMapping(value = "/customers/{id}")
    ResponseEntity<Resource<Customer>> getCustomerResource(@PathVariable Long id) {
        return this.customerRepository.findById(id)
                .map(c -> ResponseEntity.ok(this.customerResourceAssembler.toResource(c)))
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @GetMapping(value = "/customers/{id}/object")
    ResponseEntity<Customer> get(@PathVariable Long id) {
        return this.customerRepository.findById(id)
                .map(c -> ResponseEntity.ok(c))
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @PostMapping(value = "/customers")
    ResponseEntity<Resource<Customer>> post(@RequestBody Customer c) {
        Customer customer = this.customerRepository.save(new Customer(c.getFirstName(), c.getLastName(), c.getEmail(), c.getAccount()));
        URI uri = MvcUriComponentsBuilder.fromController(getClass())
                .path("/customers/{id}").buildAndExpand(customer.getId()).toUri();
        return ResponseEntity.created(uri).body(
                this.customerResourceAssembler.toResource(customer));
    }

    @DeleteMapping(value = "/customers/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        return this.customerRepository.findById(id).map(c -> {
            customerRepository.delete(c);
            return ResponseEntity.noContent().build();
        }).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    ResponseEntity<?> head(@PathVariable Long id) {
        return this.customerRepository.findById(id)
                .map(exist -> ResponseEntity.noContent().build())
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @PutMapping("/customers/{id}")
    ResponseEntity<Resource<Customer>> put(@PathVariable Long id,
                                           @RequestBody Customer c) {
        Customer customer = this.customerRepository.save(new Customer(id, c.getFirstName(), c.getLastName(), c.getEmail()));
        Resource<Customer> customerResource = this.customerResourceAssembler
                .toResource(customer);
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest()
                .toUriString());
        return ResponseEntity.created(selfLink).body(customerResource);
    }
}
