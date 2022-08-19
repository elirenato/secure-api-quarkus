package com.mycompany.config;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

public class PostgresResource implements QuarkusTestResourceLifecycleManager {

    private PostgreSQLContainer postgres;

    @Override
    public Map<String, String> start() {
        System.out.println("###################################");
        System.out.println("# Postgres Test Container started #");
        System.out.println("###################################");
        postgres = new PostgreSQLContainer("postgres:13.3")
                .withDatabaseName("app_test")
                .withUsername("app_test_user")
                .withPassword("password");
        postgres.start();
        return Map.of("quarkus.datasource.jdbc.url", postgres.getJdbcUrl());
    }

    @Override
    public void stop() {
        if (postgres != null) {
            postgres.stop();
        }
    }
}