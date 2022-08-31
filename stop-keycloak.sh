#!/bin/bash
docker-compose -f src/main/docker/keycloak/docker-compose.yaml --env-file=src/main/docker/keycloak/.env down