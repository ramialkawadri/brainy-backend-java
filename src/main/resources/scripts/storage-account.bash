#!/bin/bash

source ./init.bash

# This random string is used to ensure that the storage account name is unique
STORAGEACCOUNTNAME="brainy${RANDOM}"
SKU=Premium_LRS
KIND=BlockBlobStorage
COTNAINERNAME=brainy

echo "Creating storage account"
az storage account create \
  --name $STORAGEACCOUNTNAME \
  --resource-group $RESOURCEGROUPNAME \
  --location $LOCATION \
  --sku $SKU \
  --kind $KIND

az storage container create --name $COTNAINERNAME \
  --account-name $STORAGEACCOUNTNAME \
  --resource-group $RESOURCEGROUPNAME
