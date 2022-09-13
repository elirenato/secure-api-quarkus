package com.mycompany.resources;

import com.mycompany.common.BuilderFactory;
import com.mycompany.config.DefaultTestProfile;
import com.mycompany.entities.Country;
import com.mycompany.entities.StateProvince;
import com.mycompany.services.StateProvinceService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.NotFoundException;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@TestProfile(DefaultTestProfile.class)
@QuarkusTest
public class StateProvinceResourceTest {

    @InjectMock
    private StateProvinceService stateProvinceService;

    @Test
    @TestSecurity(user = "user")
    public void testGetStateProvinceWhenAuthenticated() {
        Country country = BuilderFactory
                .country()
                .build();
        StateProvince expectedStateProvince = BuilderFactory
                .stateProvince()
                .with(StateProvince::setCountry, country)
                .build();
        Mockito.when(stateProvinceService.getStateProvince(expectedStateProvince.getId())).thenReturn(expectedStateProvince);
        given()
                .when()
                .contentType("application/json")
                .get("/api/states-provinces/" + expectedStateProvince.getId())
                .then()
                .statusCode(200)
                .body("id", is(expectedStateProvince.getId().intValue()))
                .body("abbreviation", is(expectedStateProvince.getAbbreviation()))
                .body("name", is(expectedStateProvince.getName()))
                .body("country.id", is(expectedStateProvince.getCountry().getId().intValue()));
    }

    @Test
    public void testGetStateProvinceWithoutAuthenticationThenFail() {
        given()
                .when()
                .contentType("application/json")
                .get("/api/states-provinces/1")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user")
    public void testGetStateProvinceWhenNotFoundThenFail() {
        Mockito.when(stateProvinceService.getStateProvince(0)).thenThrow(new NotFoundException());
        given()
                .when()
                .contentType("application/json")
                .get("/api/states-provinces/0")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "user")
    public void testListAllStatesProvincesWhenAuthenticated() {
        Country country = BuilderFactory
                .country()
                .build();
        StateProvince stateProvince = BuilderFactory
                .stateProvince()
                .with(StateProvince::setCountry, country)
                .build();
        Mockito.when(stateProvinceService.listAllStatesProvinces(country.getId())).thenReturn(Arrays.asList(stateProvince));

        given()
                .when()
                .contentType("application/json")
                .get("/api/states-provinces?country=" + country.getId())
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", is(stateProvince.getId()))
                .body("[0].name", is(stateProvince.getName()));
    }

    @Test
    @TestSecurity(user = "user")
    public void testListAllStatesProvincesWithoutCountryThenFail() {
        given()
                .when()
                .contentType("application/json")
                .get("/api/states-provinces")
                .then()
                .statusCode(400);
    }

    @Test
    public void testListAllStatesProvincesWithoutAuthenticationThenFail() {
        given()
                .when()
                .contentType("application/json")
                .get("/api/states-provinces")
                .then()
                .statusCode(401);
    }
}