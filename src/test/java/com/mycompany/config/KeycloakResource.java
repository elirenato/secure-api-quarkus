package com.mycompany.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.quarkus.logging.Log;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public class KeycloakResource implements QuarkusTestResourceLifecycleManager {

    KeycloakContainer keycloak;

    @Override
    public Map<String, String> start() {
        keycloak = new KeycloakContainer();
        keycloak.start();
        return Map.of("quarkus.oidc.auth-server-url", keycloak.getAuthServerUrl() + "/realms/app");
    }

    @Override
    public void stop() {
        if (keycloak != null) {
            keycloak.stop();
        }
    }
}