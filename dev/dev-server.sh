##!/bin/bash
#if [ $1 == 'start' ]; then
#    export LOCAL_LOG_DIR=./logs
#    export IMAGE_NAMESPACE=registry.cn-hangzhou.aliyuncs.com/aliyun-cnpc-faas-dev
#    export IMAGE_TAG=local-v1beta1
#    export NODE_SERVICE_ROOT_DIR=.
#    export RESOURCE_MANAGER_PORT=10400
#    export APISERVER_PORT=10500
#    export SCHEDULER_PORT=10600
#
#    # pull远程镜像
#    docker pull "$IMAGE_NAMESPACE/nodeservice:$IMAGE_TAG"
#    docker pull "$IMAGE_NAMESPACE/containerserviceext:$IMAGE_TAG"
#
#    # 启动resourcemanager
#    docker run -d --name aliyuncnpc-resourcemanager \
#      -v /var/run/docker.sock:/var/run/docker.sock \
#      -v ${LOCAL_LOG_DIR}/resourcemanager/log:/aliyuncnpc/resourcemanager/log \
#      -e NODE_SERVICE_ROOT_DIR=$NODE_SERVICE_ROOT_DIR \
#      "$IMAGE_NAMESPACE/resourcemanager:$IMAGE_TAG"
#    resourcemanager_ipaddr=`docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' aliyuncnpc-resourcemanager`
#    endpoint="$resourcemanager_ipaddr:$RESOURCE_MANAGER_PORT"
#    export RESOURCE_MANAGER_ENDPOINT=$endpoint
#    echo "ResourceManager started, endpoint: $endpoint"
#
#    # 构建Scheduler镜像
#    mvn assembly:assembly
#    docker build -t dev-scheduler:$latest .
#    docker rmi -f `docker images | grep  "<none>" | awk '{print $3}'` # 慎用!!!清除本地none镜像
#    # 启动Scheduler
#    docker rm -f aliyuncnpc-scheduler 2> /dev/null || true 1> /dev/null
#    docker run -d -v ${LOCAL_LOG_DIR}/scheduler/log:/aliyuncnpc/scheduler/log \
#      --name aliyuncnpc-scheduler \
#      -e SERVICE_PORT=$SCHEDULER_PORT \
#      -e RESOURCE_MANAGER_ENDPOINT=$RESOURCE_MANAGER_ENDPOINT \
#      -e STACK_NAME=$STACK_NAME \
#      "$SCHEDULER_IMAGE"
#    scheduler_ipaddr=`docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' aliyuncnpc-scheduler`
#    endpoint=$scheduler_ipaddr:$SCHEDULER_PORT
#    echo "Scheduler started, endpoint: $endpoint"
#    export SCHEDULER_ENDPOINT=$endpoint
#
#    # 启动apiserver
#    docker rm -f aliyuncnpc 2> /dev/null || true 1> /dev/null
#    docker run -d -v ${LOCAL_LOG_DIR}/apiserver/log:/aliyuncnpc/apiserver/log \
#        --name aliyuncnpc-apiserver \
#        -e SCHEDULER_ENDPOINT=$SCHEDULER_ENDPOINT \
#        -e SERVICE_PORT=$APISERVER_PORT \
#        "$IMAGE_NAMESPACE/apiserver:$IMAGE_TAG"
#    apiserver_ipaddr=`docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' aliyuncnpc-apiserver`
#    export APISERVER_ENDPOINT=$apiserver_ipaddr:$APISERVER_PORT
#    echo "APIServer started, endpoint: $APISERVER_ENDPOINT"
#elif [ $1 == 'stop' ]; then
#
#fi