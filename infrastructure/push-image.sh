#!/usr/bin/env bash

: ${REPOSITORY_KEY:?"Need to set REPOSITORY_KEY variable"}
: ${APP_NAME:?"Need to set APP_NAME variable"}
: ${VERSION:?"Need to set VERSION variable"}

docker login -u oauth2accesstoken -p ${REPOSITORY_KEY} https://gcr.io
gcloud docker -- push gcr.io/kubernetes-kata/${APP_NAME}:${VERSION}