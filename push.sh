#!/bin/bash
source common.sh
build-scheduler
docker login --username=$REMOTE_REGISTRY_USERNAME registry.cn-hangzhou.aliyuncs.com
docker push $REMOTE_REGISTRY_ADDR





