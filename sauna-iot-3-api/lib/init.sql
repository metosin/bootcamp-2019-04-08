-- Launching Postgres DB with `scripts/pg-up` does creates the following user
-- and database for you.  However you can use the following DDL script as part
-- of a fallback solution should something go wrong.

CREATE USER sauna_iot_admin WITH PASSWORD 'very_secret';
CREATE DATABASE sauna_iot OWNER sauna_iot_admin;
