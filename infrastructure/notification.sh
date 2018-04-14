#!/usr/bin/env bash

: ${SLACK_URL:?"Need to set SLACK_URL variable"}
: ${MESSAGE:?"Need to set MESSAGE variable"}

curl -X POST --data-urlencode "payload=${MESSAGE}" ${SLACK_URL}