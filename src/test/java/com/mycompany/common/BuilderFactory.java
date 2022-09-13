package com.mycompany.common;

import com.github.javafaker.Faker;
import com.mycompany.entities.Country;
import com.mycompany.entities.Customer;
import com.mycompany.entities.StateProvince;

public class BuilderFactory {

    private static int manualId = 1;
    private static Faker faker = Faker.instance();

    public static Faker getFaker() {
        return faker;
    }

    public static Builder<Country> country() {
        return Builder
                .of(Country::new)
                .with(Country::setAbbreviation, faker.country().countryCode3())
                .with(Country::setName, faker.country().name())
                .with(Country::setId, manualId++);
    }

    public static Builder<StateProvince> stateProvince() {
        return Builder
                .of(StateProvince::new)
                .with(StateProvince::setAbbreviation, faker.address().stateAbbr())
                .with(StateProvince::setName, faker.address().state())
                .with(StateProvince::setId, manualId++);
    }

    public static Builder<Customer> customer() {
        return Builder
                .of(Customer::new)
                .with(Customer::setFirstName, faker.name().firstName())
                .with(Customer::setLastName, faker.name().lastName())
                .with(Customer::setEmail, faker.internet().emailAddress())
                .with(Customer::setAddress, faker.address().streetAddressNumber())
                .with(Customer::setAddress2, faker.address().secondaryAddress())
                .with(Customer::setPostalCode, faker.address().zipCode());
    }
}
