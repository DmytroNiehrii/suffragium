package io.suffragium.CustomerService;

import io.suffragium.common.entity.customer.Customer;
import io.suffragium.CustomerService.customer.CustomerRepository;
import io.suffragium.common.entity.customer.Address;
import io.suffragium.common.entity.customer.CreditCard;
import io.suffragium.common.entity.customer.CreditCardType;
import org.springframework.context.ApplicationContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles(profiles = "test")
public class ApplicationTests {
	@Autowired
	private CustomerRepository customerRepository;
    @Autowired
	private ApplicationContext applicationContext;

    public void contextLoads() throws Throwable {
        Assert.assertNotNull("the application context should have loaded.", this.applicationContext);
    }

	@Test
	public void customerTest() {
    	final String street1 = "1600 Pennsylvania Ave NW";

		CreditCard creditCard = CreditCard.builder()
				.number("1234567890")
				.type(CreditCardType.VISA)
				.build();
		Customer customer = Customer.builder()
				.firstName("Jane")
				.lastName("Doe")
				.email("jane.doe@gmail.com")
				.creditCards(new HashSet<CreditCard>())
				.addresses(new HashSet<Address>())
				.build();
		customer.getCreditCards().add(creditCard);
		customer.getAddresses().add(
				Address.builder()
						.street1(street1)
						.state("DC")
						.city("Washington")
						.country("United States")
						.zipCode(20500)
						.build()
		);

		customer = customerRepository.save(customer);
		Customer persistedResult = customerRepository.findById(customer.getId()).get();

        Assert.assertNotNull(persistedResult.getCreatedAt());
        Assert.assertNotNull(persistedResult.getLastModified());

        Assert.assertTrue(persistedResult.getAddresses().stream()
                .anyMatch(add -> add.getStreet1().equalsIgnoreCase(street1)));

        customerRepository.findByEmailContaining(customer.getEmail()).orElseThrow(
                () -> new RuntimeException("there's supposed to be a matching record!")
        );
	}

}

