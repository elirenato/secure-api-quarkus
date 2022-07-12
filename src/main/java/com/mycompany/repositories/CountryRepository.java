package com.mycompany.repositories;

import com.mycompany.entities.Country;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CountryRepository implements PanacheRepositoryBase<Country, Integer> {
}
