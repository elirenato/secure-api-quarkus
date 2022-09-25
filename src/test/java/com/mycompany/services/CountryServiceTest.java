package com.mycompany.services;

import com.mycompany.common.BuilderFactory;
import com.mycompany.config.DatabaseTestProfile;
import com.mycompany.entities.Country;
import com.mycompany.repositories.CountryRepository;
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
public class CountryServiceTest {

    private CountryService countryService;
    private CountryRepository countryRepository;

    public CountryServiceTest(CountryService countryService, CountryRepository countryRepository) {
        this.countryService = countryService;
        this.countryRepository = countryRepository;
    }

    @Test
    public void testGetCountry() {
        Country expectedEntity = BuilderFactory
                .country()
                .build(countryRepository::persist);
        Country actualEntity = this.countryService.getCountry(expectedEntity.getId());
        Assert.assertEquals(expectedEntity, actualEntity);
    }

    private List<Country> generateMockForList(int max) {
        return
            IntStream
                    .range(1, BuilderFactory.getFaker().number().numberBetween(1, max))
                    .mapToObj(n -> BuilderFactory
                            .country()
                            .build(countryRepository::persist)
                    )
                    .sorted(Comparator.comparing(Country::getName))
                    .collect(Collectors.toList());
    }

    @Test
    public void testListCountries() {
        List<Country> expectedCountries =  IntStream
                .range(1, BuilderFactory.getFaker().number().numberBetween(1, 5))
                .mapToObj(n -> BuilderFactory
                        .country()
                        .build(countryRepository::persist)
                )
                .sorted(Comparator.comparing(Country::getName))
                .collect(Collectors.toList());

        List<Country> actualCountries = this.countryService.listAllCountries();

        Assert.assertEquals(expectedCountries, actualCountries);
    }

    @Test
    public void testGetCountryWhenNotFoundThenFail() {
        Assert.assertThrows(NotFoundException.class, () -> this.countryService.getCountry(
                (int) BuilderFactory.getFaker().number().randomNumber()
        ));
    }
}
