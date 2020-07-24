##!/bin/bash
#apiserver_ipaddr=`docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' aliyuncnpc-apiserver`
#apiserver_endpoint=$apiserver_ipaddr:$APISERVER_PORT
#echo apiserverEndpoint $apiserver_endpoint
#
#docker run --name aliyuncnpc-sample-invoke $image -apiserverEndpoint $apiserver_endpoint