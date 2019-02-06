package io.suffragium.CustomerService.controller;

import io.suffragium.common.entity.customer.Customer;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;

@Component
public class CustomerResourceAssembler implements ResourceAssembler<Customer, Resource<Customer>> {

    @Override
    public Resource<Customer> toResource(Customer customer) {
        Resource<Customer> customerResource = new Resource<>(customer);
        URI photoUri = MvcUriComponentsBuilder
                .fromMethodCall(
                    MvcUriComponentsBuilder.on(CustomerProfilePhotoRestController.class)
                                .read(customer.getId())
                ).buildAndExpand().toUri();
        URI accountUri = MvcUriComponentsBuilder
                .fromMethodCall(
                        MvcUriComponentsBuilder.on(AccountRestController.class).get(customer.getAccount().getId())
                ).buildAndExpand().toUri();
        URI selfUri = MvcUriComponentsBuilder
                .fromMethodCall(
                    MvcUriComponentsBuilder.on(CustomerRestController.class)
                .getCustomerResource(customer.getId())
        ).buildAndExpand().toUri();
        URI fullObjectUri = MvcUriComponentsBuilder
                .fromMethodCall(
                        MvcUriComponentsBuilder.on(CustomerRestController.class)
                                .get(customer.getId())
                ).buildAndExpand().toUri();

        customerResource.add(new Link(selfUri.toString(), "self"));
        customerResource.add(new Link(photoUri.toString(), "profile-photo"));
        customerResource.add(new Link(accountUri.toString(), "account"));
        customerResource.add(new Link(fullObjectUri.toString(), "full-object"));
        return customerResource;
    }
}
