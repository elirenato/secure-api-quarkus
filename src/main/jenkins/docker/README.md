# How to build the Docker to Jenkins Pipeline

# Build the image

`docker build -t java11_pg13 .`

## Run the image as a container (for debug purpose)

`docker run -t -d -p 0.0.0.0:5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password -e POSTGRES_HOST_AUTH_METHOD=password -e POSTGRES_INITDB_ARGS="--auth-host=password" -e POSTGRES_DATABASES="app_test,app_test_user,password" -v "/tmp/pgdata:/var/lib/postgresql/data" -d java11_pg13`