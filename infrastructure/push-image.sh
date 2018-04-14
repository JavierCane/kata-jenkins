#!/usr/bin/env bash

: ${APP_NAME:?"Need to set APP_NAME variable"}
: ${VERSION:?"Need to set VERSION variable"}

gcloud docker -- push gcr.io/kubernetes-kata/${APP_NAME}:${VERSION}