package com.mycompany.resources;

import com.mycompany.common.BuilderFactory;
import com.mycompany.entities.Country;
import com.mycompany.services.CountryService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.NotFoundException;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class CountryResourceTest {

    @InjectMock
    private CountryService countryService;

    @Test
    @TestSecurity(user = "user")
    public void testGetCountryWhenAuthenticated() {
        Country expectedCountry = BuilderFactory
                .country()
                .build();
        Mockito.when(countryService.getCountry(expectedCountry.getId())).thenReturn(expectedCountry);
        given()
                .when()
                .contentType("application/json")
                .get("/api/countries/" + expectedCountry.getId())
                .then()
                .statusCode(200)
                .body("id", is(expectedCountry.getId().intValue()))
                .body("abbreviation", is(expectedCountry.getAbbreviation()))
                .body("name", is(expectedCountry.getName()));
    }

    @Test
    public void testGetCountryWithoutAuthenticationThenFail() {
        given()
                .when()
                .contentType("application/json")
                .get("/api/countries/1")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user")
    public void testGetCountryWhenNotFoundThenFail() {
        Mockito.when(countryService.getCountry(0)).thenThrow(new NotFoundException());
        given()
                .when()
                .contentType("application/json")
                .get("/api/countries/0")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "user")
    public void testListAllCountriesWhenAuthenticated() {
        Country country = BuilderFactory
                .country()
                .build();
        Mockito.when(countryService.listAllCountries()).thenReturn(Arrays.asList(country));

        given()
                .when()
                .contentType("application/json")
                .get("/api/countries")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", is(country.getId().intValue()))
                .body("[0].name", is(country.getName()));
    }

    @Test
    public void testListAllCountriesWithoutAuthenticationThenFail() {
        given()
                .when()
                .contentType("application/json")
                .get("/api/countries")
                .then()
                .statusCode(401);
    }
}