#!/bin/bash

# Note: run from project root directory ie. ./scripts/create-image.sh

# This script creates a docker image from the project and pushes it to a dockerhub
# image registry. This image is then referenced in the docker-compose fle in the 
# project docker folder

# the image is created using the CNCF buildpacks.io 'pack' tool
# https://buildpacks.io/docs/tools/pack/
# and the google builder buildpack (with support for java)

# create an image
pack build mbtakafkaconsumer --path ./MBTAKafkaConsumer --builder  gcr.io/buildpacks/builder:v1
# tag it
docker tag mbtakafkaconsumer:latest barrymullan/mbtakafkaconsumer:latest
# push it
docker push barrymullan/mbtakafkaconsumer:latest

# create an image
pack build mbtastreamingclient --path ./MBTAStreamingClient --builder  gcr.io/buildpacks/builder:v1
# tag it
docker tag mbtastreamingclient:latest barrymullan/mbtastreamingclient:latest
# push it
docker push barrymullan/mbtastreamingclient:latest
