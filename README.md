# Inscription Poll REST API

This is the backend repo for Arquitectura de Software subject - UNQ.

## Running

You need to download and install sbt for this application to run.

Once you have sbt installed, the following at the command prompt will start up Play in development mode:

```bash
sbt run
```

Play will start up on the HTTP port at `http://localhost:9000/`   
You don't need to deploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request.

### Database

The app expects a JDBC PostgreSql connection url in the environment variable `JDBC_DATABASE_URL` or it will fallback on configuration values. The simplest way to provide a suitable database is running:

```bash
./docker-postgresql.sh
```

Which optionally takes a volume path to save database files outside of the container (defaults to `/var/lib/postgresql/data`). 

## Running with docker-compose

```bash
docker-compose up
```

Downloads and setups both app and database containers. Play will start up on the HTTP port at `http://localhost:9000/`

## Payload samples

See [Payload samples file](Payload%20Samples) for json payload examples for some `POST`, `PATCH` or `PUT` routes.  

## Heroku

* [URL](https://ins-poll-back-arqsoft-2017s2.herokuapp.com/)
* [GIT](https://git.heroku.com/ins-poll-back-arqsoft-2017s2.git)
