# Sauna-IoT

A simple full-stack app example for demonstrating Clojure(script)

## Development

### Running the database

To start a suitable Postgres database that listensa to the port 5432 run the
following command:

```
docker run --rm --name sauna_iot_db -p 5432:5432 -d \
    -e POSTGRES_DB="sauna_iot" \
    -e POSTGRES_USER="sauna_iot_admin" \
    -e POSTGRES_PASSWORD="very_secret" \
    postgres:11.2
```

And to stop the database:

```
docker run sauna_iot_db
```

There are convenience scripts `scripts/pg-up` and `scripts/pg-down` that do exactly the above.

It is good to check that the database server is accepting connections and has
the user and default database set up correctly:

```bash
psql -h localhost -p 5432 sauna_iot sauna_iot_admin
```

Within the `psql` shell you should be able to reproduce the following:

```
Password for user sauna_iot_admin: *very_secret*
psql (11.2)
Type "help" for help.

sauna_iot=# \conninfo
You are connected to database "sauna_iot" as user "sauna_iot_admin" on host "localhost" at port "5432".
sauna_iot=# \q
```

In case you need to create the user and the database by hand see `lib/init.sql`.

### Running Figwheel and SASS compiler

Open terminal, go to the working directory and run:

```bash
lein dev
```

Then open your IDE / editor and start a new REPL from there. Use `(reset)` to
start the server and reset it when you make changes to the backend.

The frontend is running at http://localhost:3000.

The frontend will update automatically when files are saved.

## Tests

```bash
lein alt-test once
lein alt-test auto
```

## Deployment

Open terminal, go to the working directory and run:

```bash
lein prod
```

This will produce an uberjar, which you run with command:

```bash
java -jar target/uberjar/sauna-todo-0.1.0-SNAPSHOT-standalone.jar
```

## License

Copyright © 2017-2019 Metosin

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
