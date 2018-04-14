#!/usr/bin/env bash

: ${APP_NAME:?"Need to set APP_NAME variable"}
: ${VERSION:?"Need to set VERSION variable"}
: ${PORT:?"Need to set PORT variable"}

cat stack.yaml | \
sed 's/\$VERSION'"/${VERSION}/g" | \
sed 's/\$APP_NAME'"/${APP_NAME}/g" | \
sed 's/\$PORT'"/${PORT}/g" | \
kubectl apply -f -