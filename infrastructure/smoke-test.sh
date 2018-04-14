#!/usr/bin/env bash

set -e

: ${APP_NAME:?"Need to set APP_NAME variable"}
# : ${VERSION:?"Need to set VERSION variable"}
: ${PORT:?"Need to set PORT variable"}

IP=$(kubectl get service -o json | jq -r '.items[] | select(.metadata.name==env.APP_NAME) | .status.loadBalancer.ingress[0].ip')

if [[ -z ${IP} ]] ; then
    echo "Health check: Service DOWN"
    exit 1
fi

httpStatus=""
while [[ "200" != ${httpStatus} ]] ; do
    sleep 1
    {
        httpStatus=$(curl -s -o /dev/null -w "%{http_code}" ${IP}:${PORT}/health)
    } || {
        echo "Could not connect to the Web Server!"
    }
done

status=$(curl -s ${IP}:${PORT}/health || exit 1)
appStatus=$(echo ${status} | jq -r ".status")

if [[ "UP" != ${appStatus} ]] ; then
    echo "Health check: Service DOWN"
    exit 1
fi

# response=$(curl -s ${IP}:${PORT}/info | jq -r ".app .version")

echo "Health check: Service UP"