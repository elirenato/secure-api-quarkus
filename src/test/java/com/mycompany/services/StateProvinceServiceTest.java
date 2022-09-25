package com.mycompany.services;

import com.mycompany.common.BuilderFactory;
import com.mycompany.config.DatabaseTestProfile;
import com.mycompany.entities.Country;
import com.mycompany.entities.StateProvince;
import com.mycompany.repositories.CountryRepository;
import com.mycompany.repositories.StateProvinceRepository;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import javax.ws.rs.NotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@TestProfile(DatabaseTestProfile.class)
@QuarkusTest
@TestTransaction
public class StateProvinceServiceTest {

    private StateProvinceService stateProvinceService;
    private StateProvinceRepository stateProvinceRepository;
    private CountryRepository countryRepository;

    public StateProvinceServiceTest(
            StateProvinceService stateProvinceService,
            StateProvinceRepository stateProvinceRepository,
            CountryRepository countryRepository
    ) {
        this.stateProvinceService = stateProvinceService;
        this.stateProvinceRepository = stateProvinceRepository;
        this.countryRepository = countryRepository;
    }

    @Test
    public void testGetStateProvince() {
        Country country = BuilderFactory
                .country()
                .build(countryRepository::persist);
        StateProvince expectedEntity = BuilderFactory
                .stateProvince()
                .with(StateProvince::setCountry, country)
                .build(stateProvinceRepository::persist);
        StateProvince actualEntity = this.stateProvinceService.getStateProvince(expectedEntity.getId());
        Assert.assertEquals(expectedEntity, actualEntity);
    }

    private List<StateProvince> generateMockForList(Country country, int max) {
        return
            IntStream
                    .range(1, BuilderFactory.getFaker().number().numberBetween(1, max))
                    .mapToObj(n -> BuilderFactory
                            .stateProvince()
                            .with(StateProvince::setCountry, country)
                            .build(stateProvinceRepository::persist)
                    )
                    .sorted(Comparator.comparing(StateProvince::getName))
                    .collect(Collectors.toList());
    }

    @Test
    public void testListStatesProvinces() {
        Country country = BuilderFactory
                .country()
                .build(countryRepository::persist);
        List<StateProvince> expectedStateProvinces = IntStream
                .range(1, BuilderFactory.getFaker().number().numberBetween(1, 10))
                .mapToObj(n -> BuilderFactory
                        .stateProvince()
                        .with(StateProvince::setCountry, country)
                        .build(stateProvinceRepository::persist)
                )
                .sorted(Comparator.comparing(StateProvince::getName))
                .collect(Collectors.toList());

        // generate another list of countries, just to make sure the filter is applied
        Country country2 = BuilderFactory
                .country()
                .build(countryRepository::persist);
        IntStream
                .range(1, BuilderFactory.getFaker().number().numberBetween(1, 4))
                .mapToObj(n -> BuilderFactory
                        .stateProvince()
                        .with(StateProvince::setCountry, country2)
                        .build(stateProvinceRepository::persist)
                )
                .count();

        List<StateProvince> actualStatesProvinces = this.stateProvinceService.listAllStatesProvinces(country.getId());
        Assert.assertEquals(expectedStateProvinces, actualStatesProvinces);
    }

    @Test
    public void testGetStateProvinceWhenNotFoundThenFail() {
        Assert.assertThrows(NotFoundException.class, () -> this.stateProvinceService.getStateProvince(
                (int) BuilderFactory.getFaker().number().randomNumber()
        ));
    }
}
