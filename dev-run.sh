#!/bin/bash
source common.sh
if [ $1 == 'rebuild' ]; then
  build-scheduler
  ROOT_PATH=$ROOT_PATH SCHEDULER_IMAGE=$LOCAL_SCHEDULER_IMAGE_NAME CLUSTER=local NODE_PROVIDER=local bash ./stack/make/stack.sh
elif [ -z $1 ]; then
  ROOT_PATH=$ROOT_PATH SCHEDULER_IMAGE=$LOCAL_SCHEDULER_IMAGE_NAME CLUSTER=local NODE_PROVIDER=local bash ./stack/make/stack.sh
else
  echo "Unknown parameters "$1
fi
