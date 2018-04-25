#!/bin/bash

docker stop local-postgres
docker rm local-postgres
docker run \
--name local-postgres \
-v ${1:-/var/lib/postgresql/data}:/var/lib/postgresql/data \
-e POSTGRES_PASSWORD=saveallthethings \
-e POSTGRES_USER=arq-soft \
-p 5432:5432 \
postgres:10