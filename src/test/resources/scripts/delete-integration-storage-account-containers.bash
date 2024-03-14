#!/bin/bash

STORAGE_ACCOUNT_NAME=brainyintegrationtest

CONTAINERS=$(az storage container list --account-name $STORAGE_ACCOUNT_NAME --sas-token $1)

# Used to get the name from the JSON
REGEX='"name":\s?"(.*)"'

echo "$CONTAINERS" | grep -Po $REGEX | while read line; do
    CONTAINER_NAME=${line:9:-1}
    echo "Deleting ${CONTAINER_NAME}"
    az storage container delete --name $CONTAINER_NAME --account-name $STORAGE_ACCOUNT_NAME --sas-token $1 1> /dev/null
done