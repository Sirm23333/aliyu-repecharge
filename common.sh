#!/bin/bash

export ROOT_PATH=/home/sirm/文档/阿里比赛

export LOCAL_SCHEDULER_IMAGE_NAME=registry.cn-hangzhou.aliyuncs.com/sirm/java-mini-faas

export REMOTE_REGISTRY_ADDR=registry.cn-hangzhou.aliyuncs.com/sirm/java-mini-faas

export REMOTE_REGISTRY_USERNAME=sirmsyp

build-scheduler(){
  mvn assembly:assembly
  docker build -t $LOCAL_SCHEDULER_IMAGE_NAME:latest .
  docker rmi -f `docker images | grep  "<none>" | awk '{print $3}'` # 慎用!!!清除本地none镜像
}


