package com.mycompany.repositories;

import com.mycompany.entities.StateProvince;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StateProvinceRepository implements PanacheRepositoryBase<StateProvince, Integer> {
}
