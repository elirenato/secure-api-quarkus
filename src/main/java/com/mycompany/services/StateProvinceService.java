package com.mycompany.services;

import com.mycompany.entities.StateProvince;
import com.mycompany.repositories.StateProvinceRepository;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class StateProvinceService {
    private StateProvinceRepository stateProvinceRepository;

    public StateProvinceService(StateProvinceRepository stateProvinceRepository) {
        this.stateProvinceRepository = stateProvinceRepository;
    }

    public StateProvince getStateProvince(Integer id) {
        return stateProvinceRepository.findByIdOptional(id)
                .orElseThrow(NotFoundException::new);
    }

    public List<StateProvince> listAllStatesProvinces(Integer countryId)
    {
        return stateProvinceRepository.list ("country.id", Sort.by("name"), countryId);
    }
}
