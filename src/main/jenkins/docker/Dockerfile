FROM postgres:13.3

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get -y update && \
    apt-get -y install openjdk-11-jdk
RUN apt-get -y install maven

COPY ./init-user-db.sh /docker-entrypoint-initdb.d/init-user-db.sh
