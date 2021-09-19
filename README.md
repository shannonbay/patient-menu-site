# patient-menu-site

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


