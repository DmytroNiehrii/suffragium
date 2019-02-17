package io.suffragium.CustomerService;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.suffragium.CustomerService.controller.CustomerRestController;
import io.suffragium.CustomerService.customer.CustomerRepository;
import io.suffragium.common.entity.customer.Customer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static io.suffragium.CustomerService.TestUtils.lambaMatcher;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerRestController.class)
@ActiveProfiles(profiles = "test")
@ComponentScan("io.suffragium.CustomerService.controller")
public class CustomerRestControllerTest {
    @MockBean
    private CustomerRepository customerRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private MediaType jsonContentType = MediaType.parseMediaType("application/json;charset=UTF-8");
    private Customer wellKnownCustomer;
    private String rootPath = "/customers";

    @Before
    public void before() {
        this.wellKnownCustomer = Customer.builder().id(1L).firstName("Bruce").lastName("Banner").build();
        given(this.customerRepository.findById(this.wellKnownCustomer.getId()))
                .willReturn(Optional.of(this.wellKnownCustomer));
    }

    @Test
    public void testOptions() throws Throwable {
        this.mockMvc.perform(options(this.rootPath).accept(this.jsonContentType))
                .andExpect(status().isOk())
                .andExpect(header().string("Allow", notNullValue()));
    }

    @Test
    public void testGetCollection() throws Exception {

        List<Customer> customers = Arrays.asList(this.wellKnownCustomer,
                Customer.builder().id(this.wellKnownCustomer.getId() + 1).firstName("A").lastName("B").build());

        given(this.customerRepository.findAll()).willReturn(customers);

        this.mockMvc
                .perform(get(this.rootPath).accept(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(jsonContentType))
                .andExpect(
                        jsonPath("$",
                                hasSize(lambaMatcher("the count should be >= 1", (Integer i) -> i >= 1))));
    }

    @Test
    public void testGet() throws Exception {
        MvcResult result = this.mockMvc
                .perform(
                        get(this.rootPath + "/" + this.wellKnownCustomer.getId()).contentType(
                                jsonContentType).accept(jsonContentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(jsonContentType))
                .andExpect(
                        jsonPath("$.firstName", is(this.wellKnownCustomer.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(this.wellKnownCustomer.getLastName())))
                .andReturn();
    }

    @Test
    public void testPost() throws Exception {
        Customer.CustomerBuilder customerBuilder = Customer.builder().firstName("Peter").lastName("Parker").email("Peter.Parker@gmail.com");
        Customer customer = customerBuilder.build();
        String customerJSON = this.objectMapper.writeValueAsString(customer);

        Customer savedCustomer = customerBuilder.id(2L).build();
        given(this.customerRepository.save(customer)).willReturn(savedCustomer);

        String expectedContentJSON = this.objectMapper.writeValueAsString(savedCustomer);

        MvcResult result = this.mockMvc
                .perform(
                        post(this.rootPath).contentType(this.jsonContentType).content(customerJSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", notNullValue()))
                .andExpect(content().json(expectedContentJSON))
                .andReturn();
    }

    @Test
    public void testDelete() throws Exception {
        this.mockMvc.perform(
                delete(this.rootPath + "/" + this.wellKnownCustomer.getId()).contentType(
                        this.jsonContentType)).andExpect(status().isNoContent());
        verify(this.customerRepository).delete(this.wellKnownCustomer);
    }

    @Test
    public void testHead() throws Exception {
        this.mockMvc.perform(
                head(this.rootPath + "/" + this.wellKnownCustomer.getId()).contentType(
                        this.jsonContentType)).andExpect(status().isNoContent());
    }

    @Test
    public void testPut() throws Exception {

        given(this.customerRepository.findById(this.wellKnownCustomer.getId()))
                .willReturn(Optional.of(this.wellKnownCustomer));

        String fn = "Peter", ln = "Parker";
        Customer existing = this.wellKnownCustomer;
        Customer updated = Customer.builder().id(existing.getId()).firstName(fn).lastName(ln).build();
        given(this.customerRepository.save(updated)).willReturn(updated);

        String content = "{ \"id\": \"" + existing.getId() + "\", \"firstName\": \""
                + fn + "\", \"lastName\": \"" + ln + "\" }";
        String idPath = this.rootPath + "/" + existing.getId();
        this.mockMvc
                .perform(put(idPath).contentType(jsonContentType).content(content))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", notNullValue()));

        given(this.customerRepository.findById(this.wellKnownCustomer.getId()))
                .willReturn(Optional.of(updated));

        this.mockMvc.perform(get(idPath)).andExpect(jsonPath("$.firstName", is(fn)))
                .andExpect(jsonPath("$.lastName", is(ln)));

    }
}
