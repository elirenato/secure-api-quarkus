#!/bin/bash
if [ ! -f "./src/main/docker/keycloak/docker-compose.env" ]
then
  cp ./src/main/docker/keycloak/docker-compose.env.example ./src/main/docker/keycloak/docker-compose.env
fi
docker-compose -f src/main/docker/keycloak/docker-compose.yaml --env-file=./src/main/docker/keycloak/docker-compose.env down
