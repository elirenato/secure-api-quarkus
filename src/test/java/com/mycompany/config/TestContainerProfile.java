package com.mycompany.config;

import io.quarkus.test.junit.QuarkusTestProfile;

public class TestContainerProfile implements QuarkusTestProfile {
    @Override
    public String getConfigProfile() {
        return "testContainers";
    }
}
