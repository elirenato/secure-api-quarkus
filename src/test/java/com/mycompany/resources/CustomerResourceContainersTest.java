package com.mycompany.resources;

import com.mycompany.config.KeycloakResource;
import com.mycompany.config.PostgresResource;
import com.mycompany.config.TestContainerProfile;
import com.mycompany.repositories.CountryRepository;
import com.mycompany.repositories.CustomerRepository;
import com.mycompany.repositories.StateProvinceRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.TestProfile;


@QuarkusTestResource(KeycloakResource.class)
@QuarkusTestResource(PostgresResource.class)
@TestProfile(TestContainerProfile.class)
public class CustomerResourceContainersTest extends CustomerResourceTest {
    public CustomerResourceContainersTest(CustomerRepository customerRepository, StateProvinceRepository stateProvinceRepository, CountryRepository countryRepository) {
        super(customerRepository, stateProvinceRepository, countryRepository);
    }
}