#!/usr/bin/env bash

: ${GCLOUD_AUTH:?"Need to set GCLOUD_AUTH variable"}
: ${APP_NAME:?"Need to set APP_NAME variable"}
: ${VERSION:?"Need to set VERSION variable"}

gcloud auth activate-service-account --key-file ${GCLOUD_AUTH}
gcloud docker -- push gcr.io/kubernetes-kata/${APP_NAME}:${VERSION}