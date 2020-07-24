#!/bin/bash
mvn assembly:assembly
docker build -t registry.cn-hangzhou.aliyuncs.com/sirm/java-mini-faas:latest .
docker login --username=sirmsyp registry.cn-hangzhou.aliyuncs.com
docker push registry.cn-hangzhou.aliyuncs.com/sirm/java-mini-faas
docker rmi -f `docker images | grep  "<none>" | awk '{print $3}'` # 慎用!!!清除本地none镜像
