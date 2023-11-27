#!/bin/bash

# This random string is used to ensure that the storage account name is unique
STORAGEACCOUNTNAME="brainy${RANDOM}"
SKU=Premium_LRS
KIND=BlockBlobStorage
COTNAINERNAME=brainy

# Getting the parameters
RESOURCEGROUPNAME=$1
LOCATION=$2

echo "Creating storage account at RG ${RESOURCEGROUPNAME} and location ${LOCATION}"
az storage account create \
  --name $STORAGEACCOUNTNAME \
  --resource-group $RESOURCEGROUPNAME \
  --location $LOCATION \
  --sku $SKU \
  --kind $KIND

echo "Creating storage account container"
az storage container create \
  --name $COTNAINERNAME \
  --account-name $STORAGEACCOUNTNAME \
  --resource-group $RESOURCEGROUPNAME
