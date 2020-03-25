show variables like 'max_connections';
SET GLOBAL max_connections = 10;
show variables like 'max_connections';

CREATE DATABASE test;
USE test;

CREATE TABLE cities(id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(30), population BIGINT);
SHOW TABLES;

INSERT INTO cities(name, population) VALUES('Bratislava', 432000);
INSERT INTO cities(name, population) VALUES('Budapest', 1759000);
INSERT INTO cities(name, population) VALUES('Prague', 1280000);
INSERT INTO cities(name, population) VALUES('Warsaw', 1748000);
INSERT INTO cities(name, population) VALUES('Los Angeles', 3971000);
INSERT INTO cities(name, population) VALUES('New York', 8550000);
INSERT INTO cities(name, population) VALUES('Edinburgh', 464000);
INSERT INTO cities(name, population) VALUES('Berlin', 3671000);

SELECT * FROM cities;
