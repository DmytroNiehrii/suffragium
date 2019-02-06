package io.suffragium.CustomerService.account;

import io.suffragium.common.entity.customer.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
