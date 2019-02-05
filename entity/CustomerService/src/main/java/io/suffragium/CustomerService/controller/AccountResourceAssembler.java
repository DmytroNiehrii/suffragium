package io.suffragium.CustomerService.controller;

import io.suffragium.CustomerService.account.Account;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;

@Component
public class AccountResourceAssembler implements ResourceAssembler<Account, Resource<Account>> {

    @Override
    public Resource<Account> toResource(Account account) {
        Resource<Account> accountResource = new Resource<>(account);
        URI selfUri = MvcUriComponentsBuilder
                .fromMethodCall(
                    MvcUriComponentsBuilder.on(AccountRestController.class)
                .get(account.getId())
        ).buildAndExpand().toUri();

        accountResource.add(new Link(selfUri.toString(), "self"));
        return accountResource;
    }
}
