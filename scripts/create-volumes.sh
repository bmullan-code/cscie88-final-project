#!/bin/bash
# run from project root directory. eg. ./scripts/create-volumes.sh
mkdir docker/final-project
mkdir docker/final-project/volumes
mkdir docker/final-project/volumes/broker1
mkdir docker/final-project/volumes/broker1/data
mkdir docker/final-project/volumes/filebeat
cp scripts/filebeat.yml docker/final-project/volumes/filebeat/filebeat.yml
mkdir docker/final-project/volumes/filebeat/data
mkdir docker/final-project/volumes/redis
mkdir docker/final-project/volumes/zk-data
mkdir docker/final-project/volumes/zk-txn-log
sudo chmod -R a+rwx docker/final-project/volumes/redis

