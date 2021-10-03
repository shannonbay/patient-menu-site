# patient-menu Project

* jv 11
* IntelliJ Idea 2021, run with `idea`
* Run with ./gradelw quarkusDev
* Load localhost:8080/fruits

## MySQL Schema
CREATE DATABASE sch_orders;

create user 'sch_client'@'localhost' IDENTIFIED by 'clientpass';

GRANT INSERT,UPDATE ON sch_orders.* TO 'sch_client'@'localhost';

ALTER USER 'sch_client'@'localhost' WITH MAX_QUERIES_PER_HOUR 600;

create table orders ( 
    room CHAR(200) NOT NULL,
    booking_id CHAR(100) NOT NULL,
    uid MEDIUMINT NOT NULL AUTO_INCREMENT,
    small_portion_size BOOL,
    allergies JSON, preferences JSON,
    morning_tea JSON, lunch JSON, afternoon_tea JSON, dinner JSON, breakfast JSON,
    PRIMARY KEY (uid) );

create table menu_calendar (     `id` enum('1'), start DATE,     PRIMARY KEY (id) );
INSERT INTO menu_calendar (start) VALUES ('2021-09-15');

create table dinner_menu (     day tinyint NOT NULL AUTO_INCREMENT,     active BOOL NOT NULL,     name CHAR(200),     PRIMARY KEY(day), UNIQUE(name));

create table lunch_menu (
    day tinyint NOT NULL AUTO_INCREMENT,
    active BOOL,
    description CHAR,
    PRIMARY KEY (day), UNIQUE(name) );

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./gradlew build
```
It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./gradlew build -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./gradlew build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/patient-menu-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.

## Related Guides

- RESTEasy JAX-RS ([guide](https://quarkus.io/guides/rest-json)): REST endpoint framework implementing JAX-RS and more

## Provided Code

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)
