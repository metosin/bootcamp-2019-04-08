#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE bootcamp;
    CREATE SCHEMA app;
    GRANT ALL PRIVILEGES ON DATABASE bootcamp TO $POSTGRES_USER;
EOSQL