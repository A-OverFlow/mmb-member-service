# MMB Member Service [![Build Status](https://github.com/A-OverFlow/mmb-member-service/actions/workflows/ci.yml/badge.svg)](https://github.com/A-OverFlow/mmb-member-service/actions/workflows/ci.yml)

## Understanding the application with a diagrams

TBD

## Run `mmb-member-service` locally

`mmb-member-service` is a [Spring Boot](https://spring.io/guides/gs/spring-boot) application built using [Gradle](https://spring.io/guides/gs/gradle/). You can build a jar file and run it from the command line (it should work just as well with Java 21 or
newer):

```bash
git clone https://github.com/A-OverFlow/mmb-member-service.git
cd mmb-member-service
./gradlew build
java -jar build/libs/mmb-member-service.jar
```

## Building a Container

You can build a container image (if you have a docker daemon) using `Dockerfile`.

```bash
docker build -t mmb-member-service .
```

## Create Volumes And Network

```bash
docker volume create mysql_data
docker volume create minio_data
docker network create --driver bridge mmb-network
```

## Database configuration

In its default configuration, `mmb-member-service` uses MySQL.
You can start MySQL locally with whatever installer works for your OS or use docker:

```bash
docker run -d \
  --name mmb-member-service-mysql \
  -e MYSQL_ROOT_PASSWORD=demo_password \
  -e MYSQL_USER=demo_user \
  -e MYSQL_PASSWORD=demo_password \
  -e MYSQL_DATABASE=demo_db \
  -p 3306:3306 \
  -v mysql_data:/var/lib/mysql \
  --network mmb-network \
  mysql:8
```

If you see `ERROR 1524 (HY000): Plugin 'mysql_native_password' is not loaded` message, run command to clean volume.

```bash
docker volume rm mysql_data
```

## Minio configuration

```bash
docker run -d \
  --name minio \
  -e MINIO_ROOT_USER=demo_user \
  -e MINIO_ROOT_PASSWORD=demo_password \
  -p 9000:9000 \
  -p 9001:9001 \
  -v minio_data:/data \
  --network mmb-network \
  minio/minio:latest server /data --console-address ":9001"
```

After installing minio container, you should create bucket named `images` and set `Access Policy` into `Public`.

## Run `mmb-member-service` with docker-compose

Instead of vanilla `docker` you can also use the provided `docker-compose.yml` file to start the database containers.

```bash
docker-compose up --build -d 
```
