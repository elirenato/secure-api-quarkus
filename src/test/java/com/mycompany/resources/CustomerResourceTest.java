package com.mycompany.resources;

import com.mycompany.common.BuilderFactory;
import com.mycompany.config.DefaultTestProfile;
import com.mycompany.entities.Country;
import com.mycompany.entities.Customer;
import com.mycompany.entities.StateProvince;
import com.mycompany.services.CustomerService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.NotFoundException;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestProfile(DefaultTestProfile.class)
@QuarkusTest
public class CustomerResourceTest {

    @InjectMock
    private CustomerService customerService;

    private StateProvince buildStateProvince() {
        Country country = BuilderFactory
                .country()
                .build();
        return BuilderFactory
                .stateProvince()
                .with(StateProvince::setCountry, country)
                .build();
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "managers")
    public void testPersistCustomer() {
        Customer customerInput = BuilderFactory
                .customer()
                .with(Customer::setStateProvince, buildStateProvince())
                .build();

        given()
                .when()
                .body(customerInput)
                .contentType("application/json")
                .post("/api/customers")
                .then()
                .statusCode(204);

        Mockito.verify(customerService, Mockito.times(1)).persistCustomer(Mockito.refEq(customerInput));
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "managers")
    public void testPersistCustomerWhenInputIsEmpty() {
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
        Customer customerInput = BuilderFactory
                .customer()
                .with(Customer::setStateProvince, buildStateProvince())
                .with(Customer::setEmail, "jon.email.com")
                .build();
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
        Customer customerInput = BuilderFactory
                .customer()
                .with(Customer::setStateProvince, buildStateProvince())
                .build();
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
        Customer customerInput = BuilderFactory
                .customer()
                .with(Customer::setStateProvince, buildStateProvince())
                .build();
        given()
                .when()
                .body(customerInput)
                .contentType("application/json")
                .post("/api/customers")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "managers")
    public void testUpdateCustomer() {
        Customer customerInput = BuilderFactory
                .customer()
                .with(Customer::setId, BuilderFactory.getFaker().number().randomNumber())
                .with(Customer::setStateProvince, buildStateProvince())
                .build();

        given()
            .when()
            .body(customerInput)
            .contentType("application/json")
            .put("/api/customers/" + customerInput.getId())
            .then()
            .statusCode(204);

        Mockito.verify(customerService, Mockito.times(1)).updateCustomer(
                Mockito.eq(customerInput.getId()), Mockito.refEq(customerInput, "id")
        );
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "managers")
    public void testUpdateCustomerWhenInputIsEmpty() {
        Customer customerInput = new Customer();
        given()
                .when()
                .body(customerInput)
                .contentType("application/json")
                .put("/api/customers/1")
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
        Customer customerInput = BuilderFactory
                .customer()
                .with(Customer::setId, BuilderFactory.getFaker().number().randomNumber())
                .with(Customer::setStateProvince, buildStateProvince())
                .build();
        Mockito.when(customerService.updateCustomer(Mockito.any(), Mockito.any()))
                .thenThrow(new NotFoundException());
        given()
                .when()
                .contentType("application/json")
                .body(customerInput)
                .put("/api/customers/0")
                .then()
                .statusCode(404);
    }

    @Test
    public void testUpdateCustomerWithoutAuthenticationThenFail() {
        Customer customerInput = BuilderFactory
                .customer()
                .with(Customer::setId, BuilderFactory.getFaker().number().randomNumber())
                .with(Customer::setStateProvince, buildStateProvince())
                .build();
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
        Customer customerInput = BuilderFactory
                .customer()
                .with(Customer::setId, BuilderFactory.getFaker().number().randomNumber())
                .with(Customer::setStateProvince, buildStateProvince())
                .build();
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
        Long id = BuilderFactory.getFaker().number().randomNumber();

        given()
                .when()
                .contentType("application/json")
                .delete("/api/customers/" + id)
                .then()
                .statusCode(204);

        Mockito.verify(customerService, Mockito.times(1)).deleteCustomer(Mockito.eq(id));
    }

    @Test
    @TestSecurity(user = "user-manager", roles = "managers")
    public void testDeleteCustomerWhenNotFoundThenFail() {
        Long id = BuilderFactory.getFaker().number().randomNumber();
        Mockito.when(customerService.deleteCustomer(Mockito.eq(id)))
                .thenThrow(new NotFoundException());

        given()
                .when()
                .contentType("application/json")
                .delete("/api/customers/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    public void testDeleteCustomerWithoutAuthenticationThenFail() {
        Long id = BuilderFactory.getFaker().number().randomNumber();
        given()
                .when()
                .contentType("application/json")
                .delete("/api/customers/" + id)
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user-operator", roles = "operators")
    public void testDeleteCustomerWithoutAuthorizationThenFail() {
        Long id = BuilderFactory.getFaker().number().randomNumber();
        given()
                .when()
                .contentType("application/json")
                .delete("/api/customers/" + id)
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "user")
    public void testGetCustomerWhenAuthenticated() {
        Customer customer = BuilderFactory
                .customer()
                .with(Customer::setId, BuilderFactory.getFaker().number().randomNumber())
                .with(Customer::setStateProvince, buildStateProvince())
                .build();
        Mockito.when(customerService.getCustomer(Mockito.eq(customer.getId()))).thenReturn(customer);

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
    public void testGetCustomerWithoutAuthenticationThenFail() {
        Long id = BuilderFactory.getFaker().number().randomNumber();

        given()
                .when()
                .contentType("application/json")
                .get("/api/customers/" + id)
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user-operator", roles = "operators")
    public void testGetCustomerWhenNotFoundThenFail() {
        Long id = BuilderFactory.getFaker().number().randomNumber();
        Mockito.when(customerService.getCustomer(Mockito.eq(id)))
                .thenThrow(new NotFoundException());

        given()
                .when()
                .contentType("application/json")
                .get("/api/customers/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "user-operator")
    public void testListAllCustomersWhenAuthenticated() {
        StateProvince stateProvince = buildStateProvince();
        Customer customer = BuilderFactory
                .customer()
                .with(Customer::setId, BuilderFactory.getFaker().number().randomNumber())
                .with(Customer::setStateProvince, stateProvince)
                .build();
        Mockito.when(customerService.listAllCustomers()).thenReturn(Arrays.asList(customer));

        given()
            .when()
            .contentType("application/json")
            .get("/api/customers")
            .then()
            .statusCode(200)
            .body("$", hasSize(1))
            .body("[0].id", is(customer.getId().intValue()))
            .body("[0].lastName", is(customer.getLastName()))
            .body("[0].firstName", is(customer.getFirstName()));
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