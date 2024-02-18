#!/bin/bash

STORAGE_ACCOUNT_NAME=brainyintegrationtest
STORAGE_ACCOUNT_KEY=w7ILdKZcdXCUb+/+/NKnrHJ3+8Fdre7B9CsCNcSiiqsVtgkVFr5foLoG0Blm+3aU7RJMID4w0Kgj+ASt6t7klQ==

CONTAINERS=$(az storage container list --account-name $STORAGE_ACCOUNT_NAME --sas-token $STORAGE_ACCOUNT_KEY)

REGEX='"name":\s?"(.*)"'

echo "$CONTAINERS" | grep -Po $REGEX | while read line; do
    CONTAINER_NAME=${line:9:-1}
    echo "Deleting ${CONTAINER_NAME}"
    az storage container delete --name $CONTAINER_NAME --account-name $STORAGE_ACCOUNT_NAME --sas-token $STORAGE_ACCOUNT_KEY 1> /dev/null
done