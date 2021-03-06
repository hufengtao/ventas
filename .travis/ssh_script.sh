#!/bin/bash

set -e

##
## Script assumes ventas is installed and only needs to be updated,
## since the server needs to be provisioned anyway
##

cd /opt/ventas

echo "Pulling from github"
git pull --rebase --prune

echo "Installing bower deps"
bower install

echo "Building Docker images"

source /etc/docker.env

lein uberjar &&
docker build -t ventas . &&
docker build -t ventas-datomic datomic &&
echo "Restarting service" &&
rancher-compose --env-file .env -f docker-compose.prod.yml down && 
rancher-compose --env-file .env -f docker-compose.prod.yml rm && 
rancher-compose --env-file .env -f docker-compose.prod.yml up -d &&
echo "Deploy done, executing REPL commands..." &&
sleep 60 &&
CONTAINER_NAME=$(docker ps --filter "label=io.rancher.stack_service.name=ventas/ventas" --format '{{.ID}}'); \
CONTAINER_IP_MASKED=$(docker inspect -f "{{ index .Config.Labels \"io.rancher.container.ip\"}}" $CONTAINER_NAME); \
CONTAINER_IP=${CONTAINER_IP_MASKED%/16}; \
lein repl :connect ${CONTAINER_IP}:4001 << ENDREPL
(ventas.database.seed/seed :recreate? true)
(mount.core/stop #'ventas.search/indexer)
(mount.core/start #'ventas.search/indexer)
(ventas.search/reindex)
(ventas.entities.image-size/transform-all)
ENDREPL
