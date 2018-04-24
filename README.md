# Inscription Poll REST API

This is the backend repo for Arquitectura de Software subject - UNQ.

## Running

You need to download and install sbt for this application to run.

Once you have sbt installed, the following at the command prompt will start up Play in development mode:

```bash
sbt run
```

Play will start up on the HTTP port at http://localhost:9000/   
You don't need to deploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request.

## Running with docker

```bash
docker build -t inscription-poll:1.0 .

docker run -d -t -i -p 9000:9000
```

## Payload samples

See [Payload samples file](Payload%20Samples) for json payload examples for some `POST`, `PATCH` or `PUT` routes.  

## Heroku

* [URL](https://ins-poll-back-arqsoft-2017s2.herokuapp.com/)
* [GIT](https://git.heroku.com/ins-poll-back-arqsoft-2017s2.git)
