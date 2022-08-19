#!/bin/bash
set -e
IFS=';' databases=(${POSTGRES_DATABASES})
for database in "${databases[@]}"
do
    IFS=',' credentials=(${database})
    # create the user
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "
        CREATE ROLE ${credentials[1]}
        WITH LOGIN NOSUPERUSER NOINHERIT NOCREATEDB NOCREATEROLE NOREPLICATION
        PASSWORD '${credentials[2]}'"

    # create the database with the user as owner
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "
        CREATE DATABASE ${credentials[0]}
        WITH
        OWNER = ${credentials[1]}
        ENCODING = 'UTF8'"
done
