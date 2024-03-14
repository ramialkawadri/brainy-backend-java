#!/bin/bash

SKU=Premium_LRS
KIND=BlockBlobStorage

# Getting the parameters
RESOURCE_GROUP_NAME=$1
LOCATION=$2

echo -n "Enter storage account name: "
read STORAGE_ACCOUNT_NAME

echo "Creating storage account at RG ${RESOURCE_GROUP_NAME} and location ${LOCATION}"
az storage account create \
--name $STORAGE_ACCOUNT_NAME \
--resource-group $RESOURCE_GROUP_NAME \
--location $LOCATION \
--sku $SKU \
--kind $KIND
