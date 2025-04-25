CREATE DATABASE IF NOT EXISTS airlne_db;

USE airlne_db;

CREATE TABLE IF NOT EXISTS passengers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    flight_number VARCHAR(50),
    destination VARCHAR(100)
);
select * from passengers;