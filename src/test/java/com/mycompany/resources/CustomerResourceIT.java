package com.mycompany.resources;

import com.mycompany.repositories.*;

import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
public class CustomerResourceIT extends CustomerResourceTest {
    // Execute the same tests but in native mode.
    public CustomerResourceIT(CustomerRepository customerRepository, StateProvinceRepository stateProvinceRepository,
            CountryRepository countryRepository) {
        super(customerRepository, stateProvinceRepository, countryRepository);
    }
}
