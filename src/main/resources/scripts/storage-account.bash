#!/bin/bash

# This random string is used to ensure that the storage account name is unique
SKU=Premium_LRS
KIND=BlockBlobStorage
COTNAINERNAME=brainy

# Getting the parameters
RESOURCEGROUPNAME=$1
LOCATION=$2

echo -n "Enter storage account name: "
read STORAGEACCOUNTNAME

echo "Creating storage account at RG ${RESOURCEGROUPNAME} and location ${LOCATION}"
az storage account create \
--name $STORAGEACCOUNTNAME \
--resource-group $RESOURCEGROUPNAME \
--location $LOCATION \
--sku $SKU \
--kind $KIND
