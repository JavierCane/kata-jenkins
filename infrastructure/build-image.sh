#!/usr/bin/env bash

: ${APP_NAME:?"Need to set APP_NAME variable"}
: ${VERSION:?"Need to set VERSION variable"}

docker build -t gcr.io/kubernetes-kata/${APP_NAME}:${VERSION} .