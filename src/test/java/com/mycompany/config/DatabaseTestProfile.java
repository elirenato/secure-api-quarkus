package com.mycompany.config;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DatabaseTestProfile implements QuarkusTestProfile {
    @Override
    public List<TestResourceEntry> testResources() {
        boolean enableTestContainers = System.getProperty("test.containers.disabled") == null;
        if (enableTestContainers) {
            return Arrays.asList(
                    new TestResourceEntry(PostgresResource.class)
            );
        } else {
            return Collections.emptyList();
        }
    }
}
