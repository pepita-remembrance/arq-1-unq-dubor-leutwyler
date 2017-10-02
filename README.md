# Inscription Poll REST API

This is the backend repo for arquitectura de software subject - UNQ.

## Running

You need to download and install sbt for this application to run.

Once you have sbt installed, the following at the command prompt will start up Play in development mode:

```
sbt run
```

Play will start up on the HTTP port at http://localhost:9000/.   You don't need to deploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request. 

### Usage

If you call the same URL from the command line, you’ll see JSON. Using httpie, we can execute the command:

```
http --verbose http://localhost:9000/v1/careers
```

and get back:

```
GET /v1/careers HTTP/1.1
```

Likewise, you can also create a Career directly as JSON:

```
http --verbose POST http://localhost:9000/v1/careers title="hello" description="world"
```

and get:

```
POST /v1/careers HTTP/1.1
```

## Heroku

[URL](https://murmuring-beyond-94607.herokuapp.com/v1/careers)
