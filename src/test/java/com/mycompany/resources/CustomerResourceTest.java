package com.mycompany.resources;

import com.github.javafaker.Faker;
import com.mycompany.entities.*;
import com.mycompany.repositories.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
public class CustomerResourceTest {

    private Faker faker = Faker.instance();

    private CustomerRepository customerRepository;
    private StateProvinceRepository stateProvinceRepository;
    private CountryRepository countryRepository;

    public CustomerResourceTest(
            CustomerRepository customerRepository,
            StateProvinceRepository stateProvinceRepository,
            CountryRepository countryRepository
    ) {
        this.customerRepository = customerRepository;
        this.stateProvinceRepository = stateProvinceRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional
    protected StateProvince mockStateProvince() {
        Country country = new Country();
        country.setId(1);
        country.setAbbreviation(faker.country().countryCode3());
        country.setName(faker.country().name());
        countryRepository.persist(country);

        StateProvince stateProvince = new StateProvince();
        stateProvince.setId(1);
        stateProvince.setName(faker.address().state());
        stateProvince.setAbbreviation(faker.address().stateAbbr());
        stateProvince.setCountry(country);
        stateProvinceRepository.persist(stateProvince);
        return stateProvince;
    }

    private Customer createCustomerInput() {
        Customer customerInput = new Customer();
        customerInput.setFirstName(faker.name().firstName());
        customerInput.setLastName(faker.name().lastName());
        customerInput.setEmail(faker.internet().emailAddress());
        customerInput.setAddress(faker.address().fullAddress());
        customerInput.setAddress2(faker.address().secondaryAddress());
        customerInput.setPostalCode(faker.address().zipCode());
        customerInput.setStateProvince(
                stateProvinceRepository.findByIdOptional(1)
                        .orElseGet(this::mockStateProvince)
        );
        return customerInput;
    }

    private void assertEntityWasUpdated(Long entityId, Customer customerInput) {
        Customer customerEntity = customerRepository.findById(entityId);
        assertEquals(customerEntity.getFirstName(), customerInput.getFirstName());
        assertEquals(customerEntity.getLastName(), customerInput.getLastName());
        assertEquals(customerEntity.getEmail(), customerInput.getEmail());
        assertEquals(customerEntity.getAddress(), customerInput.getAddress());
        assertEquals(customerEntity.getAddress2(), customerInput.getAddress2());
        assertEquals(customerEntity.getPostalCode(), customerInput.getPostalCode());
        assertEquals(customerEntity.getStateProvince().getId(), customerInput.getStateProvince().getId());
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "managers")
    public void testPersistCustomer() {
        // Mock some input data to send in the request
        Customer customerInput = createCustomerInput();
        // Make the post request and assert the response body comes with the entity
        given()
                .when()
                .body(customerInput)
                .contentType("application/json")
                .post("/api/customers")
                .then()
                .statusCode(204);
        customerRepository.find("email", customerInput.getEmail()).firstResult();
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "managers")
    public void testPersistCustomerWhenInputIsEmpty() {
        // mock an empty input for the request
        Customer customerInput = new Customer();
        given()
                .when()
                .body(customerInput)
                .contentType("application/json")
                .post("/api/customers")
                .then()
                .statusCode(400)
                .body("violations", hasSize(6))
                .body("violations", hasItems(
                        allOf(
                                hasEntry("field", "persistCustomer.customerInput.firstName"),
                                hasEntry("message", "must not be blank")
                        ),
                        allOf(
                                hasEntry("field", "persistCustomer.customerInput.lastName"),
                                hasEntry("message", "must not be blank")
                        ),
                        allOf(
                                hasEntry("field", "persistCustomer.customerInput.email"),
                                hasEntry("message", "must not be blank")
                        ),
                        allOf(
                                hasEntry("field", "persistCustomer.customerInput.address"),
                                hasEntry("message", "must not be blank")
                        ),
                        allOf(
                                hasEntry("field", "persistCustomer.customerInput.postalCode"),
                                hasEntry("message", "must not be blank")
                        ),
                        allOf(
                                hasEntry("field", "persistCustomer.customerInput.stateProvince"),
                                hasEntry("message", "must not be null")
                        )
                ));
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "managers")
    public void testPersistCustomerWhenEmailIsInvalid() {
        Customer customerInput = createCustomerInput();
        customerInput.setEmail("jon.email.com");
        given()
                .when()
                .body(customerInput)
                .contentType("application/json")
                .post("/api/customers")
                .then()
                .statusCode(400)
                .body("violations", hasSize(1))
                .body("violations", hasItems(allOf(
                        hasEntry("message", "must be a well-formed email address"),
                        hasEntry("field", "persistCustomer.customerInput.email")
                )));
    }

    @Test
    public void testPersistCustomerWithoutAuthenticationThenFail() {
        Customer customerInput = createCustomerInput();
        given()
                .when()
                .body(customerInput)
                .contentType("application/json")
                .post("/api/customers")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user-operator", roles = "operators")
    public void testPersistCustomerWithoutAuthorizationThenFail() {
        Customer customerInput = createCustomerInput();
        given()
                .when()
                .body(customerInput)
                .contentType("application/json")
                .post("/api/customers")
                .then()
                .statusCode(403);
    }

    @Transactional
    protected Customer mockCustomerToGetOrUpdate() {
        Customer customer = createCustomerInput();
        customerRepository.persist(customer);
        return customer;
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "managers")
    public void testUpdateCustomer() {
        final Customer customerEntity = mockCustomerToGetOrUpdate();
        // mock some data to update the customer information
        Customer customerInput = createCustomerInput();
        given()
            .when()
            .body(customerInput)
            .contentType("application/json")
            .put("/api/customers/" + customerEntity.getId())
            .then()
            .statusCode(204);
        assertEntityWasUpdated(customerEntity.getId(), customerInput);
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "managers")
    public void testUpdateCustomerWhenInputIsEmpty() {
        final Customer customerEntity = mockCustomerToGetOrUpdate();
        // mock an empty input for the request
        Customer customerInput = new Customer();
        given()
                .when()
                .body(customerInput)
                .contentType("application/json")
                .put("/api/customers/" + customerEntity.getId())
                .then()
                .statusCode(400)
                .body("violations", hasSize(6))
                .body("violations", hasItems(
                        allOf(
                                hasEntry("field", "updateCustomer.customerInput.firstName"),
                                hasEntry("message", "must not be blank")
                        ),
                        allOf(
                                hasEntry("field", "updateCustomer.customerInput.lastName"),
                                hasEntry("message", "must not be blank")
                        ),
                        allOf(
                                hasEntry("field", "updateCustomer.customerInput.email"),
                                hasEntry("message", "must not be blank")
                        ),
                        allOf(
                                hasEntry("field", "updateCustomer.customerInput.address"),
                                hasEntry("message", "must not be blank")
                        ),
                        allOf(
                                hasEntry("field", "updateCustomer.customerInput.postalCode"),
                                hasEntry("message", "must not be blank")
                        ),
                        allOf(
                                hasEntry("field", "updateCustomer.customerInput.stateProvince"),
                                hasEntry("message", "must not be null")
                        )
                ));
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "managers")
    public void testUpdateCustomerWhenNotFoundThenFail() {
        Customer author = createCustomerInput();
        given()
                .when()
                .contentType("application/json")
                .body(author)
                .put("/api/customers/0")
                .then()
                .statusCode(404);
    }

    @Test
    public void testUpdateCustomerWithoutAuthenticationThenFail() {
        Customer customerInput = createCustomerInput();
        given()
                .when()
                .body(customerInput)
                .contentType("application/json")
                .put("/api/customers/1")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "operators")
    public void testUpdateCustomerWithoutAuthorizationThenFail() {
        Customer customerInput = createCustomerInput();
        given()
                .when()
                .body(customerInput)
                .contentType("application/json")
                .put("/api/customers/1")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "managers")
    public void testDeleteCustomer() {
        Customer author = mockCustomerToGetOrUpdate();
        given()
            .when()
            .contentType("application/json")
            .delete("/api/customers/" + author.getId())
            .then()
            .statusCode(204);
        Customer authorDeleted = customerRepository.findById(author.getId());
        assertNull(authorDeleted);
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "managers")
    public void testDeleteCustomerWhenNotFoundThenFail() {
        given()
                .when()
                .contentType("application/json")
                .delete("/api/customers/0")
                .then()
                .statusCode(404);
    }

    @Test
    public void testDeleteCustomerWithoutAuthenticationThenFail() {
        given()
                .when()
                .contentType("application/json")
                .delete("/api/customers/1")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user-operator", roles = "operators")
    public void testDeleteCustomerWithoutAuthorizationThenFail() {
        given()
                .when()
                .contentType("application/json")
                .delete("/api/customers/1")
                .then()
                .statusCode(403);
    }

    private void getCustomer() {
        Customer customer = mockCustomerToGetOrUpdate();
        given()
                .when()
                .contentType("application/json")
                .get("/api/customers/" + customer.getId())
                .then()
                .statusCode(200)
                .body("id", is(customer.getId().intValue()))
                .body("firstName", is(customer.getFirstName()))
                .body("lastName", is(customer.getLastName()))
                .body("email", is(customer.getEmail()))
                .body("address", is(customer.getAddress()))
                .body("address2", is(customer.getAddress2()))
                .body("postalCode", is(customer.getPostalCode()))
                .body("stateProvince.id", is(customer.getStateProvince().getId().intValue()));
    }

    @Test
    @TestSecurity(user = "user-operator", roles = "operators")
    public void testGetCustomerWhenOperator() {
        getCustomer();
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "managers")
    public void testGetCustomerWhenManager() {
        getCustomer();
    }

    @Test
    public void testGetCustomerWithoutAuthenticationThenFail() {
        given()
                .when()
                .contentType("application/json")
                .get("/api/customers/1")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user-operator", roles = "operators")
    public void testGetCustomerWhenNotFoundThenFail() {
        given()
                .when()
                .contentType("application/json")
                .get("/api/customers/0")
                .then()
                .statusCode(404);
    }

    @Transactional
    protected Customer[] mockCustomersToList() {
        customerRepository.deleteAll();

        Customer customer1 = createCustomerInput();
        customer1.setLastName("Stark");
        customer1.setFirstName("Ned");
        customerRepository.persist(customer1);

        Customer customer2 = createCustomerInput();
        customer2.setLastName("Stark");
        customer2.setFirstName("Catelyn");
        customerRepository.persist(customer2);

        Customer customer3 = createCustomerInput();
        customer3.setLastName("Bolton");
        customer3.setFirstName("Roose");
        customerRepository.persist(customer3);

        Customer customer4 = createCustomerInput();
        customer4.setLastName("Bolton");
        customer4.setFirstName("Ramsey");
        customerRepository.persist(customer4);

        return new Customer[]{customer1, customer2, customer3, customer4};
    }

    private void listAllCustomers() {
        // Persisted order
        // [0]: Stark|Ned
        // [1]: Stark|Catelyn
        // [2]: Bolton|Roose
        // [3]: Bolton|Ramsey
        Customer[] authors = mockCustomersToList();

        // list without filter, neither sort. The default sort will be applied (lastName asc, firstName asc)
        // Expected order
        // [3]: Bolton|Ramsey
        // [2]: Bolton|Roose
        // [1]: Stark|Catelyn
        // [0]: Stark|Ned
        given()
                .when()
                .contentType("application/json")
                .get("/api/customers")
                .then()
                .statusCode(200)
                .body("$", hasSize(4))
                .body("[0].lastName", is(authors[3].getLastName()))
                .body("[0].firstName", is(authors[3].getFirstName()))
                .body("[1].lastName", is(authors[2].getLastName()))
                .body("[1].firstName", is(authors[2].getFirstName()))
                .body("[2].lastName", is(authors[1].getLastName()))
                .body("[2].firstName", is(authors[1].getFirstName()));
    }

    @Test
    @TestSecurity(user = "user-operator", roles = "operators")
    public void testListAllCustomersWhenOperator() {
        listAllCustomers();
    }

    @Test
    @TestSecurity(user = "user-operator", roles = "managers")
    public void testListAllCustomersWhenManager() {
        listAllCustomers();
    }

    @Test
    public void testListAllCustomersWithoutAuthenticationThenFail() {
        given()
                .when()
                .contentType("application/json")
                .get("/api/customers")
                .then()
                .statusCode(401);
    }
}