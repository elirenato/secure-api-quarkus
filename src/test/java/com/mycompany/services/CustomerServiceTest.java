package com.mycompany.services;

import com.mycompany.common.BuilderFactory;
import com.mycompany.config.DatabaseTestProfile;
import com.mycompany.entities.Country;
import com.mycompany.entities.Customer;
import com.mycompany.entities.StateProvince;
import com.mycompany.repositories.CountryRepository;
import com.mycompany.repositories.CustomerRepository;
import com.mycompany.repositories.StateProvinceRepository;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import javax.ws.rs.NotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@TestProfile(DatabaseTestProfile.class)
@QuarkusTest
@TestTransaction
public class CustomerServiceTest {

    private CustomerService customerService;
    private CustomerRepository customerRepository;
    private StateProvinceRepository stateProvinceRepository;
    private CountryRepository countryRepository;

    public CustomerServiceTest(CustomerService customerService, CustomerRepository customerRepository, StateProvinceRepository stateProvinceRepository, CountryRepository countryRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.stateProvinceRepository = stateProvinceRepository;
        this.countryRepository = countryRepository;
    }

    private StateProvince buildStateProvince() {
        Country country = BuilderFactory
                .country()
                .build(countryRepository::persist);
        return BuilderFactory
                .stateProvince()
                .with(StateProvince::setCountry, country)
                .build(stateProvinceRepository::persist);
    }

    @Test
    public void testPersistCustomer() {
        Customer customerInput = BuilderFactory
                .customer()
                .with(Customer::setStateProvince, buildStateProvince())
                .build();

        customerService.persistCustomer(customerInput);

        Customer actualCustomer = customerRepository.find("email", customerInput.getEmail()).firstResult();
        Assert.assertNotNull(actualCustomer);
        customerInput.setId(actualCustomer.getId());
        Assert.assertEquals(customerInput.toString(), actualCustomer.toString());
    }

    @Test
    public void testUpdateCustomer() {
        Customer customerPersisted = BuilderFactory
                .customer()
                .with(Customer::setStateProvince, buildStateProvince())
                .build(customerRepository::persist);
        Customer customerInput = BuilderFactory
                .customer()
                .with(Customer::setStateProvince, buildStateProvince())
                .build();

        customerService.updateCustomer(customerPersisted.getId(), customerInput);

        customerPersisted = customerRepository.findById(customerPersisted.getId());
        customerInput.setId(customerPersisted.getId());
        Assert.assertEquals(customerInput.toString(), customerPersisted.toString());
    }

    @Test
    public void testUpdateCustomerWhenNotFoundThenFail() {
        Customer customerInput = BuilderFactory
                .customer()
                .with(Customer::setId, BuilderFactory.getFaker().number().randomNumber())
                .with(Customer::setStateProvince, buildStateProvince())
                .build();

        Assert.assertThrows(NotFoundException.class, () ->
                customerService.updateCustomer(customerInput.getId(), customerInput)
        );
    }

    @Test
    public void testGetCustomer() {
        Customer expectedCustomer = BuilderFactory
            .customer()
            .with(Customer::setStateProvince, buildStateProvince())
            .build(customerRepository::persist);

        Customer actualCustomer = customerService.getCustomer(expectedCustomer.getId());

        Assert.assertEquals(expectedCustomer.toString(), actualCustomer.toString());
    }

    @Test
    public void testGetCustomerWhenNotFoundThenFail() {
        Assert.assertThrows(NotFoundException.class, () ->
                customerService.getCustomer(BuilderFactory.getFaker().number().randomNumber())
        );
    }

    @Test
    public void testListCustomer() {
        StateProvince stateProvince = buildStateProvince();
        List<Customer> expectedCustomers = IntStream
                .range(1, BuilderFactory.getFaker().number().numberBetween(1, 10))
                .mapToObj(n -> BuilderFactory
                        .customer()
                        .with(Customer::setStateProvince, stateProvince)
                        .build(customerRepository::persist)
                )
                .sorted(Comparator
                        .comparing(Customer::getLastName)
                        .thenComparing(Customer::getFirstName)
                )
                .collect(Collectors.toList());

        List<Customer> actualCustomers = customerService.listAllCustomers();

        Assert.assertEquals(expectedCustomers, actualCustomers);
    }

    @Test
    public void testDeleteCustomer() {
        Customer expectedCustomer = BuilderFactory
                .customer()
                .with(Customer::setStateProvince, buildStateProvince())
                .build(customerRepository::persist);

        Customer actualCustomer = customerService.deleteCustomer(expectedCustomer.getId());

        Assert.assertEquals(expectedCustomer.toString(), actualCustomer.toString());
    }


    @Test
    public void testDeleteCustomerWhenNotFoundThenFail() {
        Assert.assertThrows(NotFoundException.class, () ->
                customerService.deleteCustomer(BuilderFactory.getFaker().number().randomNumber())
        );
    }
}
