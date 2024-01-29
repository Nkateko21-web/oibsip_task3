CREATE DATABASE atm_database;
USE atm_database;

CREATE TABLE IF NOT EXISTS users (
    userId VARCHAR(255) PRIMARY KEY,
    pin VARCHAR(4),
    balance DOUBLE
);



INSERT INTO users (user_id, pin, balance) VALUES ('97030', '9101', 2000.0);