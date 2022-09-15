package com.mycompany.resources;

import com.fasterxml.jackson.annotation.JsonView;
import com.mycompany.entities.Country;
import com.mycompany.entities.Customer;
import com.mycompany.services.CountryService;
import io.quarkus.security.Authenticated;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@Path("/api/countries")
public class CountryResource {

    private CountryService countryService;

    public CountryResource(CountryService countryService) {
        this.countryService = countryService;
    }

    @Path("/{id}")
    @GET
    @Authenticated
    public Country getCountry(@PathParam("id") Integer id) {
        return countryService.getCountry(id);
    }

    @GET
    @Authenticated
    public List<Country> listAllCountries() {
        return countryService.listAllCountries();
    }
}