#!/bin/bash
docker-compose -f src/main/docker/keycloak/docker-compose.yaml --env-file=src/main/docker/keycloak/.env build
docker-compose -f src/main/docker/keycloak/docker-compose.yaml --env-file=src/main/docker/keycloak/.env up -d
docker-compose -f src/main/docker/keycloak/docker-compose.yaml --env-file=src/main/docker/keycloak/.env logs -f