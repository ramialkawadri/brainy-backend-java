#!/bin/bash

STORAGE_ACCOUNT_NAME=brainyintegrationtest

az login

CONTAINERS=$(az storage container list --account-name $STORAGE_ACCOUNT_NAME)

REGEX='"name":\s?"(.*)"'

echo "$CONTAINERS" | grep -Po $REGEX | while read line; do
    CONTAINER_NAME=${line:9:-1}
    echo "Deleting ${CONTAINER_NAME}"
    az storage container delete --name $CONTAINER_NAME --account-name $STORAGE_ACCOUNT_NAME
done