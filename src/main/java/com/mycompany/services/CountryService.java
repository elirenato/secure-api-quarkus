package com.mycompany.services;

import com.mycompany.entities.Country;
import com.mycompany.repositories.CountryRepository;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class CountryService {
    private CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public Country getCountry(Integer id) {
        return countryRepository.findByIdOptional(id)
                .orElseThrow(NotFoundException::new);
    }

    public List<Country> listAllCountries() {
        return countryRepository.listAll(Sort.by("name"));
    }
}
