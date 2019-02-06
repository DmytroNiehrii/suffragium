package io.suffragium.CustomerService.controller;

import io.suffragium.CustomerService.account.AccountRepository;
import io.suffragium.common.entity.customer.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/hal+json")
public class AccountRestController {
    private final AccountResourceAssembler accountResourceAssembler;
    private AccountRepository accountRepository;

    @Autowired
    AccountRestController(AccountResourceAssembler accountResourceAssembler, AccountRepository accountRepository) {
        this.accountResourceAssembler = accountResourceAssembler;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/accounts")
    ResponseEntity<Resources<Resource<Account>>> getCollection() {
        List<Resource<Account>> collect = this.accountRepository.findAll().stream()
                .map(accountResourceAssembler::toResource)
                .collect(Collectors.<Resource<Account>>toList());
        Resources<Resource<Account>> resources = new Resources<>(collect);
        URI self = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        resources.add(new Link(self.toString(), "self"));
        return ResponseEntity.ok(resources);
    }

    @GetMapping(value = "/accounts/{id}")
    ResponseEntity<Resource<Account>> get(@PathVariable Long id) {
        return this.accountRepository.findById(id)
                .map(c -> ResponseEntity.ok(this.accountResourceAssembler.toResource(c)))
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @PostMapping(value = "/accounts")
    ResponseEntity<Resource<Account>> post(@RequestBody Account c) {
        Account account = this.accountRepository.save(new Account(c.getAccountNumber(), c.getAddresses()));
        URI uri = MvcUriComponentsBuilder.fromController(getClass())
                .path("/accounts/{id}").buildAndExpand(account.getId()).toUri();
        return ResponseEntity.created(uri).body(
                this.accountResourceAssembler.toResource(account));
    }

    @DeleteMapping(value = "/accounts/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        return this.accountRepository.findById(id).map(c -> {
            accountRepository.delete(c);
            return ResponseEntity.noContent().build();
        }).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @PutMapping("/accounts/{id}")
    ResponseEntity<Resource<Account>> put(@PathVariable Long id,
                                           @RequestBody Account c) {
        Account customer = this.accountRepository.save(new Account(id, c.getAccountNumber(), c.getCreditCards(), c.getAddresses()));
        Resource<Account> accountResource = this.accountResourceAssembler
                .toResource(customer);
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest()
                .toUriString());
        return ResponseEntity.created(selfLink).body(accountResource);
    }
}
