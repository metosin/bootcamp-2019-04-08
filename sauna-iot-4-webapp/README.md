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
docker stop sauna_iot_db
```

There are convenience scripts `scripts/pg-up` and `scripts/pg-down` that do
exactly the above.

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

In case you need to create the user and the database by hand see
`lib/init.sql`.

### Running the backend

Open a REPL and within the `user` namespace evaluate:

```clojure
user> (go)
```

To restart the system evaluate:

```clojure
user> (reset)
```

Once the backend system is running you can access:

- the API end-points through http://localhost:3000/api
- the Swagger UI for the API through http://localhost:3000/api/index.html
- the Swagger specification for the API through http://localhost:3000/api/swagger.json

### Running the frontend

Open terminal, go to the root project directory and run:

```bash
lein dev
```

(Unfortunately the `pdo` command that the `dev` command invokes behind tends
to hang.  If this happens, follow the instructions below.)

The `lein dev` command runs `sass4clj` and `figwheel` commands in parallel.
You can also run them by hand.  Run the following commands in separate
terminals.

```bash
lein sass4clj auto
```

```base
lein figwheel
```

Once you the following line in the Figwheel terminal

```
Prompt will show when Figwheel connects to your application
```

you can open the front-end client by pointing your browser to
http://localhost:3000.  Try open multiple clients in different browser tabs or
browsers.

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
