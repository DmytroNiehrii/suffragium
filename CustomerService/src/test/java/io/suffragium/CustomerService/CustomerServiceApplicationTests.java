package io.suffragium.CustomerService;

import com.mysql.cj.exceptions.AssertionFailedException;
import io.suffragium.CustomerService.account.Account;
import io.suffragium.CustomerService.address.Address;
import io.suffragium.CustomerService.creditcard.CreditCard;
import io.suffragium.CustomerService.creditcard.CreditCardType;
import io.suffragium.CustomerService.customer.Customer;
import io.suffragium.CustomerService.customer.CustomerRepository;
import org.springframework.context.ApplicationContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CustomerServiceApplication.class)
@ActiveProfiles(profiles = "test")
public class CustomerServiceApplicationTests {
	@Autowired
	private CustomerRepository customerRepository;
    @Autowired
	private ApplicationContext applicationContext;

    public void contextLoads() throws Throwable {
        Assert.assertNotNull("the application context should have loaded.", this.applicationContext);
    }

	@Test
	public void customerTest() {
		Account account = new Account("12345");
		Customer customer = new Customer("Jane", "Doe", "jane.doe@gmail.com", account);
		CreditCard creditCard = new CreditCard("1234567890", CreditCardType.VISA);
		customer.getAccount().getCreditCards().add(creditCard);

		String street1 = "1600 Pennsylvania Ave NW";
		Address address = new Address(street1, null, "DC", "Washington", "United States", 20500);
		customer.getAccount().getAddresses().add(address);

		customer = customerRepository.save(customer);
		Customer persistedResult = customerRepository.findById(customer.getId()).get();

		Assert.assertNotNull(persistedResult.getAccount());
        Assert.assertNotNull(persistedResult.getCreatedAt());
        Assert.assertNotNull(persistedResult.getLastModified());

        Assert.assertTrue(persistedResult.getAccount().getAddresses().stream()
                .anyMatch(add -> add.getStreet1().equalsIgnoreCase(street1)));

        customerRepository.findByEmailContaining(customer.getEmail()).orElseThrow(
                () -> new AssertionFailedException(new RuntimeException("there's supposed to be a matching record!"))
        );
	}

}

